package io.github.kvxd.unsafeKt.api

import io.github.kvxd.unsafeKt.memory.Address
import io.github.kvxd.unsafeKt.memory.MemoryContext

/**
 * Represents a hierarchical data structure similar to classes or structs in other languages.
 * Uses an implementation [io.github.kvxd.unsafeKt.memory.MemoryProvider] for manipulating memory of child components.
 */
abstract class HierarchyStructure(private val base: Address, private val scope: UnsafeScope = globalScope) {

    protected fun int32(offset: Address) = (base + offset).int32(scope)

}