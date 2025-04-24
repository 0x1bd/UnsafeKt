package io.github.kvxd.unsafeKt.memory

abstract class MemoryProvider(private val pid: Int) {

    abstract fun attach()
    abstract fun detach()

    abstract fun readInt32(address: Address): Int
    abstract fun writeInt32(address: Address, value: Int)

}