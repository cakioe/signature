package io.github

import io.github.cakioe.Signatory
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

        val isOk = this.signer.checkSignature(result, result["sign"] as String)

        println("checkSignature_test: $isOk")
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
}