package io.github.kvxd.unsafeKt.platform.unix

import io.github.kvxd.unsafeKt.memory.Address
import io.github.kvxd.unsafeKt.memory.MemoryProvider
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
class UnixMemoryProvider(private val pid: Int) : MemoryProvider(pid) {

    private var memFile: CPointer<FILE>? = null

    override fun attach() {
        check(geteuid() == 0u) {
            "Root privileges required: current UID is ${geteuid()}"
        }

        val path = "/proc/$pid/mem"
        memFile = fopen(path, "r+b")
            ?: error("Failed to open $path. Ensure you are root or have ptrace access. (errno=${posix_errno()})")
    }

    override fun detach() {
        memFile?.let {
            fclose(it)
            memFile = null
        }
    }

    override fun readInt32(address: Address): Int = memScoped {
        val buffer = alloc<IntVar>()

        fseek(memFile, address.convert(), SEEK_SET)
        val bytesRead = fread(buffer.ptr, sizeOf<IntVar>().convert(), 1.convert(), memFile)
        check(bytesRead == 1uL) { "Failed to read memory at 0x${address.toString(16)} (errno=${posix_errno()})" }

        buffer.value
    }

    override fun writeInt32(address: Address, value: Int) = memScoped {
        val buffer = alloc<IntVar>()

        buffer.value = value
        fseek(memFile, address.convert(), SEEK_SET)

        val bytesWritten = fwrite(buffer.ptr, sizeOf<IntVar>().convert(), 1.convert(), memFile)
        check(bytesWritten == 1uL) { "Failed to write memory at 0x${address.toString(16)} (errno=${posix_errno()})" }
    }

}