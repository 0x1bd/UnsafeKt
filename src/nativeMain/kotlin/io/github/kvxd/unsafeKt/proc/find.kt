package io.github.kvxd.unsafeKt.proc

import kotlinx.cinterop.*
import platform.posix.closedir
import platform.posix.opendir
import platform.posix.readdir
import platform.posix.readlink

@OptIn(ExperimentalForeignApi::class)
fun findProcessIdByExecutable(executableNameOrPath: String): Int? {
    val dir = opendir("/proc") ?: return null

    try {
        while (true) {
            val entry = readdir(dir) ?: break
            val name = entry.pointed.d_name.toKString()

            if (!name.all { it.isDigit() }) continue

            val pid = name.toIntOrNull() ?: continue
            val exePath = "/proc/$pid/exe"

            val resolved = ByteArray(512)

            val len = readlink(exePath, resolved.refTo(0), resolved.size.convert())
            if (len > 0) {
                val actualPath = resolved.decodeToString(0, len.toInt())

                if (executableNameOrPath == actualPath
                    || actualPath.contains("/$executableNameOrPath")
                )
                    return pid
            }
        }
    } finally {
        closedir(dir)
    }

    return null
}