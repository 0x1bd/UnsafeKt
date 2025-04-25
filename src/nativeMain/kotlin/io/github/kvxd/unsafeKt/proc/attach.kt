package io.github.kvxd.unsafeKt.proc

import io.github.kvxd.unsafeKt.UnsafeScope

fun UnsafeScope.attach(process: Process) {
    mem = process.createBackend()
    targetProcess = process
    mem.attach()
}