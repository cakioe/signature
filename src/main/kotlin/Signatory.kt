package com.cakioe.signatory

import com.google.gson.Gson
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util.*

interface Server {
    fun genSignature(params: Map<String, Any>): String
    fun toBase64String(params: Map<String, Any>): String
    fun checkSignature(params: Map<String, Any>, sign: String): Boolean
}

class Signatory(private val appKey: String = "") : Server {
    override fun genSignature(params: Map<String, Any>): String {
        // 如果存在 "sign"，则删除
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

    override fun toBase64String(params: Map<String, Any>): String {
        val payload = params.toMutableMap().apply {
            // 如果不存在 "timestamp"，则追加一个当前时间戳(秒级时间戳)
            if (!containsKey("timestamp")) {
                put("timestamp", Instant.now().epochSecond.toString())
            }

            // 如果不存在 "sign"，则追加一个签名
            if (!containsKey("sign")) {
                put("sign", genSignature(this))
            }
        }

        // 将 map 转换为 json
        val reply = Gson().toJson(payload).toByteArray()
        return Base64.getEncoder().encodeToString(reply)
    }

    override fun checkSignature(params: Map<String, Any>, sign: String): Boolean {
        return sign == genSignature(params)
    }
}