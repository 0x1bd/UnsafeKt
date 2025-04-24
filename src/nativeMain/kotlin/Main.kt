import io.github.kvxd.unsafeKt.proc.Process
import io.github.kvxd.unsafeKt.proc.attach
import io.github.kvxd.unsafeKt.unsafeKtCatching

fun main() = unsafeKtCatching {
    attach(Process.byId(10089) ?: error("Process not found"))
    println("Found process: ${targetProcess!!.pid}")

    val bytes = memoryBackend.read(0x62f4394ee074, 32)
    val intValue = bytes[0].toInt() and 0xFF or
            (bytes[1].toInt() and 0xFF shl 8) or
            (bytes[2].toInt() and 0xFF shl 16) or
            (bytes[3].toInt() and 0xFF shl 24)

    println("Integer value: $intValue")
}