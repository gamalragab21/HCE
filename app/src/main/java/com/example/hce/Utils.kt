package com.example.hce

object Utils {

    const val BrodCastAction="broadcast_my-custom-action"
    fun hexStringToByteArray(data: String): ByteArray =
        data.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    fun toHex(byteArray: ByteArray): String = byteArray.joinToString("") { "%02x".format(it) }

    private val HEX_CHARS_ARRAY = "0123456789ABCDEF".toCharArray()
    fun toHex2(byteArray: ByteArray): String {
        val result = StringBuffer()

        byteArray.forEach {
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS_ARRAY[firstIndex])
            result.append(HEX_CHARS_ARRAY[secondIndex])
        }

        return result.toString()
    }

    fun toByteArray(text:String)= text.toByteArray()
}