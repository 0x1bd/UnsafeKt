package io.github.kvxd.unsafeKt.proc

import io.github.kvxd.unsafeKt.UnsafeScope

fun UnsafeScope.attach(process: Process) {
    memoryBackend = process.createBackend()
    targetProcess = process
    memoryBackend.attach()
}