package io.github.kvxd.unsafeKt

import io.github.kvxd.unsafeKt.mem.MemoryBackend
import io.github.kvxd.unsafeKt.proc.Process

@UnsafeKtDSL
fun unsafeKt(scope: UnsafeScope.() -> Unit) {
    val unsafeScope = UnsafeScope()

    unsafeScope.scope()

    unsafeScope.memoryBackend.detach()
}

@UnsafeKtDSL
fun unsafeKtCatching(scope: UnsafeScope.() -> Unit) {
    try {
        unsafeKt(scope)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

class UnsafeScope {

    var targetProcess: Process? = null
    internal lateinit var memoryBackend: MemoryBackend

}