package io.github.cakioe

import com.google.gson.Gson
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

/**
 * This is an interface for service
 *
 * @author cleveng
 */
interface Service {
    fun genSignature(params: Map<String, Any>): String
    fun toBase64String(params: Map<String, Any>): String
    fun checkSignature(params: Map<String, Any>, sign: String): Boolean
}

/**
 * This is a class for mqtt service.
 * It supports data sign and data encryption and data verification.
 *
 * <p>Example usage:</p>
 * <pre>
 *     var signer = Signatory("apikey");
 *     signer.genSignature(params);
 *     signer.checkSignature(params, sign);
 *     signer.toBase64String(params);
 * </pre>
 *
 * @author cleveng
 * @version 1.0.0
 * @since 1.0.0
 */
class Signatory(private val appKey: String = "") : Service {
    /**
     * gen signature with params
     *
     * @param params: Map<String, Any>
     * @return String
     * @since 1.0.0
     */
    override fun genSignature(params: Map<String, Any>): String {
        /**
         * if exists "sign", remove
         */
        val payload = params.toMutableMap().apply {
            if (containsKey("sign")) {
                remove("sign")
            }
        }

        val result = payload
            .toSortedMap() // 按字典序排序
            .filterValues { it != null && it != "" && it != 0 && it != 0L } // 过滤掉空值和 0
            .entries
            .joinToString("&") { (key, value) ->
                "${key}=${URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.toString())}"
            }

        val body = "${result}&key=$appKey".toByteArray(StandardCharsets.UTF_8)

        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(body)
        val bigInt = BigInteger(1, digest)
        return bigInt.toString(16).padStart(32, '0').uppercase()
    }

    /**
     * gen base64 string from params
     *
     * @param params: Map<String, Any>
     * @return String
     * @since 1.0.0
     */
    override fun toBase64String(params: Map<String, Any>): String {
        val payload = params.toMutableMap().apply {
            /**
             * if not exist "timestamp", then append a timestamp (second-level timestamp)
             */
            if (!containsKey("timestamp")) {
                put("timestamp", Instant.now().epochSecond.toString())
            }

            /**
             * if not exist "sign", then append a signature
             */
            if (!containsKey("sign")) {
                put("sign", genSignature(this))
            }
        }

        // 将 map 转换为 json
        val reply = Gson().toJson(payload).toByteArray()
        return Base64.getEncoder().encodeToString(reply)
    }

    /**
     * check signature with params and sign
     *
     * @param params: Map<String, Any>
     * @param sign: String
     * @return Boolean
     * @since 1.0.0
     */
    override fun checkSignature(params: Map<String, Any>, sign: String): Boolean {
        return sign == genSignature(params)
    }
}