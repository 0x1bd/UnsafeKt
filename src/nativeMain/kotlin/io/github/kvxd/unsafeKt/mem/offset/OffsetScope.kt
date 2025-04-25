package io.github.kvxd.unsafeKt.mem.offset

import io.github.kvxd.unsafeKt.UnsafeScope

abstract class Struct(
    val base: Long,
    val unsafe: UnsafeScope
)