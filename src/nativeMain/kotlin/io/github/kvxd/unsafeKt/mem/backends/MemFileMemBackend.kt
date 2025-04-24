package io.github.kvxd.unsafeKt.mem.backends

import io.github.kvxd.unsafeKt.mem.MemoryAccessException
import io.github.kvxd.unsafeKt.mem.MemoryBackend
import io.github.kvxd.unsafeKt.proc.PermissionDeniedException
import io.github.kvxd.unsafeKt.proc.Process
import io.github.kvxd.unsafeKt.proc.ProcessNotFoundException
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
class MemFileMemBackend(private val process: Process) : MemoryBackend(process) {

    private var memFile: CPointer<FILE>? = null

    override fun attach() {
        memFile = fopen("/proc/${process.pid}/mem", "r+").also {
            if (it == null) {
                val err = posix_errno()
                val msg = strerror(err)?.toKString() ?: "Unknown error"
                when (err) {
                    EACCES -> throw PermissionDeniedException("Access denied to process memory: $msg")
                    ENOENT -> throw ProcessNotFoundException("Process not found (${process.pid}): $msg")
                    else -> throw RuntimeException("Failed to open process memory: $msg")
                }
            }
        }

        if (fseeko(memFile, 0, SEEK_SET) != 0) {
            val err = posix_errno()
            val msg = strerror(err)?.toKString() ?: "Unknown error"
            throw RuntimeException("Memory seek initialization failed: $msg")
        }
    }

    override fun detach() {
        memFile?.let {
            fclose(it)
            memFile = null
        }
    }

    override fun read(address: Long, size: Int): ByteArray {
        checkAttached()

        return ByteArray(size).also { buffer ->
            buffer.usePinned { pinned ->
                seek(address)

                val bytesRead = fread(
                    pinned.addressOf(0),
                    1.convert(),
                    size.convert(),
                    memFile
                ).toInt()

                if (bytesRead != size) {
                    handleIOError(address, "Read", bytesRead, size)
                }
            }
        }
    }

    override fun write(address: Long, data: ByteArray) {
        checkAttached()

        data.usePinned { pinned ->
            seek(address)

            val bytesWritten = fwrite(
                pinned.addressOf(0),
                1.convert(),
                data.size.convert(),
                memFile
            ).toInt()

            if (bytesWritten != data.size) {
                handleIOError(address, "Write", bytesWritten, data.size)
            }
        }
    }

    private fun checkAttached() {
        if (memFile == null) {
            throw IllegalStateException("Not attached to process memory")
        }
    }

    private fun seek(address: Long) {
        if (fseeko(memFile, address, SEEK_SET) != 0) {
            val err = posix_errno()
            val msg = strerror(err)?.toKString() ?: "Unknown error"
            when (err) {
                EINVAL -> throw IllegalArgumentException("Invalid address: 0x${address.toString(16)}")
                EACCES -> throw PermissionDeniedException("Access denied at address 0x${address.toString(16)}")
                else -> throw RuntimeException("Seek failed at 0x${address.toString(16)}: $msg")
            }
        }
    }

    private fun handleIOError(address: Long, operation: String, actual: Int, expected: Int) {
        val err = posix_errno()
        val msg = strerror(err)?.toKString() ?: "Unknown error"
        when (err) {
            EIO -> throw MemoryAccessException(
                "$operation failed at 0x${address.toString(16)} - " +
                        "memory not accessible ($actual/$expected bytes processed)"
            )
            EFAULT -> throw MemoryAccessException(
                "$operation failed at 0x${address.toString(16)} - " +
                        "invalid buffer address ($actual/$expected bytes processed)"
            )
            else -> throw RuntimeException(
                "$operation failed at 0x${address.toString(16)} " +
                        "($actual/$expected bytes processed): $msg"
            )
        }
    }
}
