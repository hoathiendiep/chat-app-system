package com.example.chatapp.domain.common.utils

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.*

object UuidUtils {
    val random: SecureRandom = SecureRandom()


    fun randomV7(): UUID {
        val value = randomBytes()
        val buf: ByteBuffer = ByteBuffer.wrap(value)
        val high: Long = buf.getLong()
        val low: Long = buf.getLong()
        return UUID(high, low)
    }

    fun randomBytes(): ByteArray {
        val value = ByteArray(16)
        random.nextBytes(value)

        val timestamp: ByteBuffer = ByteBuffer.allocate(java.lang.Long.BYTES)
        timestamp.putLong(System.currentTimeMillis())

        System.arraycopy(timestamp.array(), 2, value, 0, 6)

        value[6] = ((value[6].toInt() and 0x0F) or 0x70).toByte()
        value[8] = ((value[8].toInt() and 0x3F) or 0x80).toByte()

        return value
    }
}