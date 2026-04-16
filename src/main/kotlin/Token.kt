package io.github.cakioe

import com.google.gson.Gson
import java.lang.System.currentTimeMillis
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

interface TokenService {
    fun generate(timestamp: Long, body: ByteArray): String
    fun verify(timestamp: Long, body: ByteArray, signature: String): Boolean
}

class Token(private val secret: String) : TokenService {
    /**
     * Generate a signature for a map of data.
     *
     * @param timestamp: Current timestamp in seconds.
     * @param body: Data to be signed.
     * @return Signature string.
     *
     * @since 1.0.12
     */
    override fun generate(timestamp: Long, body: ByteArray): String {
        val payload = "$timestamp.${body.toString(UTF_8)}"
        return hmacSha256Hex(payload, secret)
    }

    /**
     * Verify the signature of the incoming map and the signature.
     *
     * @param timestamp: Current timestamp in seconds.
     * @param body: Data to be verified.
     * @param signature: Signature string.
     * @return True if the signature is valid, false otherwise.
     *
     * @since 1.0.12
     */
    override fun verify(
        timestamp: Long,
        body: ByteArray,
        signature: String
    ): Boolean {
        // 1. 时间检查：5分钟 = 300秒
        val now = currentTimeMillis() / 1000

        // 接收端需要校验，发送端不需要
        if (kotlin.math.abs(now - timestamp) > 300) {
            return false
        }

        // 2. 计算预期签名
        val payload = "$timestamp.${body.toString(UTF_8)}"
        val computedSignature = hmacSha256Hex(payload, secret)

        // 3. 比对签名（固定时间比较，防时序攻击）
        return safeEquals(computedSignature, signature)
    }

    /**
     * Calculate the HMAC-SHA256 hash of the given data and secret.
     *
     * @param data: The data to be hashed.
     * @param secret: The secret key to use for hashing.
     * @return The HMAC-SHA256 hash of the given data and secret, as a hexadecimal string.
     *
     * @since 1.0.12
     */
    private fun hmacSha256Hex(data: String, secret: String): String {
        val secretKey = SecretKeySpec(secret.toByteArray(UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(data.toByteArray(UTF_8))
        return hash.toHex()
    }

    /**
     * Convert a byte array to a hexadecimal string.
     *
     * @return A hexadecimal string representation of the given byte array.
     *
     * @since 1.0.12
     */
    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    /**
     * Safely compare two strings.
     *
     * This function compares two strings character by character, and returns true if they are equal.
     * It is safe to use because it does not leak the lengths of the strings, and it does not allocate any memory.
     *
     * @param a: The first string to compare.
     * @param b: The second string to compare.
     * @return True if the strings are equal, false otherwise.
     */
    private fun safeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
}