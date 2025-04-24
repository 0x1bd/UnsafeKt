import io.github.kvxd.unsafeKt.api.HierarchyStructure
import io.github.kvxd.unsafeKt.api.globalScope
import io.github.kvxd.unsafeKt.api.int32
import io.github.kvxd.unsafeKt.proc.findProcessIdByExecutable
import io.github.kvxd.unsafeKt.api.unsafe
import io.github.kvxd.unsafeKt.memory.Address
import platform.posix.waitpid

var money by 0x14562eb8L.int32()

fun main() {
    unsafe(18483) {
        memory {
            useUnixProvider()
        }

        println(money)
        money = 69
        println(money)
    }
}