import io.github.kvxd.unsafeKt.mem.InternalUnsafeApi
import io.github.kvxd.unsafeKt.mem.float
import io.github.kvxd.unsafeKt.mem.offset.Struct
import io.github.kvxd.unsafeKt.mem.offset.float
import io.github.kvxd.unsafeKt.mem.offset.int32
import io.github.kvxd.unsafeKt.proc.Process
import io.github.kvxd.unsafeKt.proc.attach
import io.github.kvxd.unsafeKt.unsafeKtCatching

@OptIn(InternalUnsafeApi::class)
fun main() = unsafeKtCatching {
    attach(Process.byName("game_debug") ?: error("Process not found (game_debug)"))
    println("Found process: ${targetProcess!!.pid}")

    val player = object : Struct(0x57666b5c7070, this) {
        var health by float(0x0)
        var score by int32(0x4)
    }

    // requires opt-in
    val globalHealth by float(0x57666b5c7070)

    println(globalHealth)
    println(player.health)
    println(player.score)
}