package io.github.kvxd.unsafeKt.mem

import io.github.kvxd.unsafeKt.proc.Process

abstract class MemoryBackend(private val process: Process) {

    abstract fun attach()
    abstract fun detach()

    abstract fun read(address: Long, size: Int): ByteArray
    abstract fun write(address: Long, data: ByteArray)

}