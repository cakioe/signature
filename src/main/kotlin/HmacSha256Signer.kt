package io.github.cakioe

import com.google.gson.JsonParser
import org.erdtman.jcs.JsonCanonicalizer
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface HmacSha256SignerTrait {
    fun sign(body: ByteArray, timestamp: Long? = null): Pair<Long, String>
    fun verify(
        body: ByteArray,
        timestamp: Long,
        signature: String,
        tolerance: Long? = null
    ): Boolean
}

class HmacSha256Signer(private val secret: String) : HmacSha256SignerTrait {
    /**
     * 获取当前 Unix 时间戳（秒）
     */
    private fun currentTimestamp(): Long {
        return System.currentTimeMillis() / 1000
    }

    /**
     * 构建签名内容
     *
     * 移除 sign、timestamp 字段
     * 使用 RFC 8785 (JCS) 规范化 JSON
     */
    private fun buildSignBody(input: ByteArray): ByteArray {
        val json = JsonParser.parseString(
            input.toString(Charsets.UTF_8)
        )

        require(json.isJsonObject) {
            "sign body must be a JSON object"
        }

        val objectJson = json.asJsonObject.deepCopy()

        objectJson.remove("sign")
        objectJson.remove("timestamp")

        val canonical = JsonCanonicalizer(
            objectJson.toString()
        )

        return canonical.encodedString.toByteArray(Charsets.UTF_8)
    }

    /**
     * 计算 HMAC-SHA256
     *
     * timestamp.body
     */
    private fun calculate(
        body: ByteArray,
        timestamp: Long
    ): String {
        val mac = Mac.getInstance("HmacSHA256")

        mac.init(
            SecretKeySpec(
                secret.toByteArray(Charsets.UTF_8),
                "HmacSHA256"
            )
        )

        val payload = timestamp.toString()
            .toByteArray(Charsets.UTF_8) +
                byteArrayOf('.'.code.toByte()) +
                body

        val signature = mac.doFinal(payload)

        return signature.joinToString("") {
            "%02x".format(it.toInt() and 0xff)
        }
    }

    /**
     * 生成签名
     */
    override fun sign(body: ByteArray, timestamp: Long?): Pair<Long, String> {
        val ts = timestamp ?: currentTimestamp()

        val payload = buildSignBody(body)
        val signature = calculate(payload, ts)

        return Pair(ts, signature)
    }

    /**
     * 验证签名
     */
    override fun verify(
        body: ByteArray,
        timestamp: Long,
        signature: String,
        tolerance: Long?
    ): Boolean {
        // 1. 时间窗口检查
        if (tolerance != null) {
            require(tolerance >= 0) {
                "tolerance must not be negative"
            }

            val now = currentTimestamp()

            if (kotlin.math.abs(
                    now.toDouble() - timestamp.toDouble()
                ) > tolerance.toDouble()
            ) {
                throw IllegalArgumentException(
                    "signature verify failed, timestamp is out of time window"
                )
            }
        }

        // 2. 计算预期签名
        val payload = buildSignBody(body)
        val expected = calculate(payload, timestamp)

        // 3. Hex 解码
        val receivedBytes = try {
            require(signature.length == 64)
            require(signature.all {
                it in '0'..'9' || it in 'a'..'f' ||
                        it in 'A'..'F'
            })

            signature.chunked(2).map {
                it.toInt(16).toByte()
            }.toByteArray()
        } catch (_: Exception) {
            throw IllegalArgumentException(
                "invalid signature format: expected hexadecimal"
            )
        }

        val expectedBytes = expected.chunked(2).map {
            it.toInt(16).toByte()
        }.toByteArray()

        // 4. 恒定时间比较
        return MessageDigest.isEqual(
            expectedBytes,
            receivedBytes
        )
    }
}