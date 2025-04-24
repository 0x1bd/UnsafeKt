package io.github.kvxd.unsafeKt.memory

import io.github.kvxd.unsafeKt.api.UnsafeScope
import io.github.kvxd.unsafeKt.platform.unix.UnixMemoryProvider

class MemoryScope(private val context: MemoryContext, private val pid: Int) {

    fun useUnixProvider() {
        (context as UnsafeScope).mem = UnixMemoryProvider(pid)
    }
}