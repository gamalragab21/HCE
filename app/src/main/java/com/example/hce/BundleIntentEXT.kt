package com.example.hce

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

 fun ByteArray.bytesToHex(): String {
    return this.joinToString("") { String.format("%02X", it) }
}

 fun String.hexToBytes(): ByteArray {
    val len = length / 2
    val result = ByteArray(len)
    for (i in 0 until len) {
        result[i] = substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    return result
}

 fun String.hexToString(): String {
    val bytes = hexToBytes()
    return String(bytes)
}