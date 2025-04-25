package io.github.kvxd.unsafeKt.mem

import io.github.kvxd.unsafeKt.UnsafeScope
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object ByteConverter {
    fun intToBytesLe(value: Int): ByteArray = ByteArray(4) { i ->
        ((value shr (i * 8)) and 0xFF).toByte()
    }

    fun bytesLeToInt(bytes: ByteArray): Int {
        require(bytes.size >= 4) { "Need at least 4 bytes for Int" }
        return bytes[0].toInt() and 0xFF shl 0 or
                (bytes[1].toInt() and 0xFF shl 8) or
                (bytes[2].toInt() and 0xFF shl 16) or
                (bytes[3].toInt() and 0xFF shl 24)
    }

    fun shortToBytesLe(value: Short): ByteArray = ByteArray(2) { i ->
        ((value.toInt() shr (i * 8)) and 0xFF).toByte()
    }

    fun bytesLeToShort(bytes: ByteArray): Short {
        require(bytes.size >= 2) { "Need at least 2 bytes for Short" }
        return ((bytes[0].toInt() and 0xFF) or
                (bytes[1].toInt() and 0xFF shl 8)).toShort()
    }

    fun longToBytesLe(value: Long): ByteArray = ByteArray(8) { i ->
        ((value shr (i * 8)) and 0xFF).toByte()
    }

    fun bytesLeToLong(bytes: ByteArray): Long {
        require(bytes.size >= 8) { "Need at least 8 bytes for Long" }
        var result = 0L
        for (i in 0 until 8) {
            result = result or ((bytes[i].toLong() and 0xFF) shl (i * 8))
        }
        return result
    }

    fun floatToBytesLe(value: Float): ByteArray = intToBytesLe(value.toBits())

    fun bytesLeToFloat(bytes: ByteArray): Float = Float.fromBits(bytesLeToInt(bytes))

    fun doubleToBytesLe(value: Double): ByteArray = longToBytesLe(value.toBits())

    fun bytesLeToDouble(bytes: ByteArray): Double = Double.fromBits(bytesLeToLong(bytes))
}

@InternalUnsafeApi
fun UnsafeScope.int32(address: Long) = object : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        val bytes = mem.read(address, 32)
        return ByteConverter.bytesLeToInt(bytes)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        mem.write(address, ByteConverter.intToBytesLe(value))
    }
}

@InternalUnsafeApi
fun UnsafeScope.int8(address: Long) = object : ReadWriteProperty<Any?, Byte> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Byte {
        val bytes = mem.read(address, 8)
        return bytes[0]
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Byte) {
        mem.write(address, byteArrayOf(value))
    }
}

@InternalUnsafeApi
fun UnsafeScope.int16(address: Long) = object : ReadWriteProperty<Any?, Short> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Short {
        val bytes = mem.read(address, 16)
        return ByteConverter.bytesLeToShort(bytes)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Short) {
        mem.write(address, ByteConverter.shortToBytesLe(value))
    }
}

@InternalUnsafeApi
fun UnsafeScope.float(address: Long) = object : ReadWriteProperty<Any?, Float> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        val bytes = mem.read(address, 32)
        return ByteConverter.bytesLeToFloat(bytes)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        mem.write(address, ByteConverter.floatToBytesLe(value))
    }
}

@InternalUnsafeApi
fun UnsafeScope.double(address: Long) = object : ReadWriteProperty<Any?, Double> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        val bytes = mem.read(address, 64)
        return ByteConverter.bytesLeToDouble(bytes)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        mem.write(address, ByteConverter.doubleToBytesLe(value))
    }
}