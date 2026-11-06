package io.github

import com.google.gson.Gson
import io.github.cakioe.HmacSha256Signer
import io.github.cakioe.Signatory
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val signer: Signatory = Signatory("testkey")

    private val payload: Map<String, Any> = mapOf(
        "key1" to "1",
        "key2" to "2",
        "key3" to "3"
    )

    /**
     * test prepareParams function
     *
     * @since 1.0.8
     */
    @Test
    fun prepareParams_test() {
        val result = this.signer.prepareParams(this.payload)
        result.forEach { (k, v) ->
            println("prepareParams_test key: $k (${k::class}), value: $v (${v::class})")
        }
    }

    /**
     * Test genSignature function.
     *
     * @since 1.0.8
     */
    @Test
    fun genSignature_test() {
        val result = this.signer.genSignature(this.payload)
        println("genSignature_test: $result")
    }

    /**
     * Test toBase64String function.
     *
     * This function tests that the toBase64String function can correctly
     * convert a map of data to a base64 encoded string.
     *
     * @since 1.0.8
     */
    @Test
    fun toBase64String_test() {
        val result = this.signer.toBase64String(this.payload)
        println("toBase64String_test: $result")
    }

    /**
     * Test checkSignature function.
     *
     * This function tests that the checkSignature function can correctly
     * verify the signature of a map of data.
     *
     * @since 1.0.8
     */
    @Test
    fun checkSignature_test() {
        val params =
            "eyJrZXkxIjoiMSIsImtleTIiOiIyIiwia2V5MyI6IjMiLCJ0aW1lc3RhbXAiOiIxNzU4MTg0NTIzIiwic2lnbiI6IjNFNjYxQUVBNUJCNEIwNzEyNzRFREM2MjkwODlDQTM0In0="
        val result = this.signer.decryptBase64String(params)

        val isValid = this.signer.checkSignature(result, result["sign"] as String)

        assertTrue(isValid)
    }

    /**
     * Test decryptBase64String function.
     *
     * This function tests that the decryptBase64String function can correctly
     * decrypt a base64 encoded string to a map of data.
     *
     * @since 1.0.8
     */
    @Test
    fun decryptBase64String_test() {
        val params =
            "eyJrZXkxIjoiMSIsImtleTIiOiIyIiwia2V5MyI6IjMiLCJ0aW1lc3RhbXAiOiIxNzU4MTg0NTIzIiwic2lnbiI6IjNFNjYxQUVBNUJCNEIwNzEyNzRFREM2MjkwODlDQTM0In0="
        val result = this.signer.decryptBase64String(params)
        println("decryptBase64String_test: $result")
    }

    /**
     * Test token function.
     *
     * This function tests that the token function can correctly
     * generate a signature for a map of data and verify the signature.
     *
     * @since 1.0.12
     */
    @Test
    fun token_test() {
        val token = this.signer.token()
        val timestamp = System.currentTimeMillis() / 1000

        val payload = Gson().toJson(
            mapOf("serial_no" to "da4f77d59e13935a")
        ).toByteArray()

        // 生成签名
        val signature = token.generate(timestamp, payload)
        println("signature: $signature")

        // 验证签名
        val isValid = token.verify(timestamp, payload, signature)
        println("verify: $isValid")

        assertTrue(isValid)
    }

    @Test
    fun hmac_sha256_signer_test() {
        val signer = HmacSha256Signer("your-secret")

        data class TestPayload(
            val a: Int,
            val b: Int,
            val c: Boolean,
            val d: String?
        )

        // 第一步：构建对象
        val body = TestPayload(
            a = 1,
            b = 2,
            c = true,
            d = null
        )

        // 第二步：序列化，查看 JSON
        val json = Gson().toJson(body)
        println("JSON: $json")

        // 转换为 ByteArray
        val payload = json.toByteArray(Charsets.UTF_8)

        // 生成签名
        val (timestamp, signature) = signer.sign(payload)

        println("timestamp: $timestamp")
        println("signature: $signature")

        // 验证签名
        val isValid = signer.verify(
            body = payload,
            timestamp = timestamp,
            signature = signature,
            tolerance = 300
        )

        println("verify: $isValid")

        assertTrue(isValid)
    }
}