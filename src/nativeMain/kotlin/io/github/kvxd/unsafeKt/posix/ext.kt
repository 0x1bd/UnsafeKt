package io.github.kvxd.unsafeKt.posix

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.pin
import platform.posix.DIR
import platform.posix.FILE
import platform.posix.closedir
import platform.posix.fclose

@OptIn(ExperimentalForeignApi::class)
fun <T> CPointer<FILE>?.use(block: (CPointer<FILE>) -> T): T? {
    if (this == null) return null

    try {
        return block(this)
    } finally {
        fclose(this)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun <T> CPointer<DIR>?.use(block: (CPointer<DIR>) -> T): T? {
    if (this == null) return null

    try {
        return block(this)
    } finally {
        closedir(this)
    }
}