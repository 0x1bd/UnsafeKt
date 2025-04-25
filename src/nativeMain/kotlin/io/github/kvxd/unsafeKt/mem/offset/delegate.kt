package io.github.kvxd.unsafeKt.mem.offset

import io.github.kvxd.unsafeKt.mem.*

@OptIn(InternalUnsafeApi::class)
fun Struct.int32(offset: Long) = unsafe.int32(base + offset)

@OptIn(InternalUnsafeApi::class)
fun Struct.int8(offset: Long) = unsafe.int8(base + offset)

@OptIn(InternalUnsafeApi::class)
fun Struct.int16(offset: Long) = unsafe.int16(base + offset)

@OptIn(InternalUnsafeApi::class)
fun Struct.float(offset: Long) = unsafe.float(base + offset)

@OptIn(InternalUnsafeApi::class)
fun Struct.double(offset: Long) = unsafe.double(base + offset)