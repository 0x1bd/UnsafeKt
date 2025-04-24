package io.github.kvxd.unsafeKt.api

import io.github.kvxd.unsafeKt.memory.MemoryProvider
import io.github.kvxd.unsafeKt.memory.MemoryContext
import io.github.kvxd.unsafeKt.memory.MemoryScope

internal lateinit var globalScope: UnsafeScope

fun <R> unsafe(pid: Int, function: UnsafeScope.() -> R): R {
    val scope = UnsafeScope(pid)
    globalScope = scope
    try {
        return scope.function()
    } finally {
        scope.mem.detach()
    }
}

class UnsafeScope(private val pid: Int) : MemoryContext {
    override lateinit var mem: MemoryProvider

    fun memory(block: MemoryScope.() -> Unit) {
        MemoryScope(this, pid).apply(block)
    }
}