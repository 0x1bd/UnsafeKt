package io.github.kvxd.unsafeKt.api

import io.github.kvxd.unsafeKt.memory.Address
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Address.int32(scope: UnsafeScope = globalScope) = object : ReadWriteProperty<Any?, Int> {

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        scope.mem.writeInt32(this@int32, value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int =
        scope.mem.readInt32(this@int32)
}
