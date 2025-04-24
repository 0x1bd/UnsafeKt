package io.github.kvxd.unsafeKt.proc

import io.github.kvxd.unsafeKt.mem.MemoryBackend
import io.github.kvxd.unsafeKt.mem.backends.MemFileMemBackend
import io.github.kvxd.unsafeKt.posix.use
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
data class Process(val pid: Int) {

    val name by lazy { readProcessName() }
    val executablePath by lazy { readExecutablePath() }
    val commandLine by lazy { readCommandLine() }

    val isAlive: Boolean get() = kill(pid.convert(), 0) == 0

    private fun readProcessName(): String =
        readProcFile("status")
            ?.lines()
            ?.firstOrNull { it.startsWith("Name:") }
            ?.substringAfter('\t')
            ?: "unknown"

    private fun readExecutablePath(): String {
        val path = "/proc/$pid/exe"
        val buffer = ByteArray(4096)

        return buffer.usePinned { pinned ->
            val count = readlink(path, pinned.addressOf(0), buffer.size.convert())
            if (count > 0) pinned.get().decodeToString(0, count.toInt()) else path
        }
    }

    private fun readCommandLine(): List<String> =
        readProcFile("cmdline")
            ?.split('\u0000')
            ?.filterNot { it.isBlank() }
            ?: emptyList()

    private fun readProcFile(file: String): String? {
        val path = "/proc/$pid/$file"
        return fopen(path, "r")?.use { file ->
            val content = buildString {
                val buffer = ByteArray(4096)
                while (true) {
                    val bytesRead = fread(buffer.refTo(0), 1.convert(), buffer.size.convert(), file)
                    if (bytesRead <= 0u) break
                    append(buffer.decodeToString(0, bytesRead.convert()))
                }
            }
            content.ifEmpty { null }
        }
    }

    fun createBackend(): MemoryBackend {
        if (!isAlive) throw IllegalStateException("Process is not running")
        return MemFileMemBackend(this)
    }

    companion object {
        fun byName(name: String): Process? {
            return all().firstOrNull { it.name == name }
        }

        fun byId(pid: Int): Process? {
            return if (access("/proc/$pid", F_OK) == 0) Process(pid) else null
        }

        private fun all(): List<Process> {
            return listProcesses()
        }

        private fun listProcesses(): List<Process> {
            val processes = mutableListOf<Process>()

            opendir("/proc").use {
                while (true) {
                    val entry = readdir(it) ?: break
                    val name = entry.pointed.d_name.toKString()

                    if (name.toIntOrNull() == null) continue
                    if (name == "." || name == "..") continue

                    val pid = name.toInt()
                    if (kill(pid.convert(), 0) != 0) continue  // Check if process exists

                    processes.add(Process(pid))
                }
            }

            return processes
        }

    }


}
