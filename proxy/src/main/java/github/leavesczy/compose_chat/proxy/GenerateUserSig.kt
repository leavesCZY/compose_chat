package github.leavesczy.compose_chat.proxy

import android.util.Base64
import org.json.JSONObject
import java.util.zip.Deflater
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
internal class GenerateUserSig(
    private val appId: Int,
    private val appSecretKey: String
) {

    fun genUserSig(userId: String): String {
        return genTLSSignature(
            appId = appId,
            secretKey = appSecretKey,
            userId = userId,
            expireTime = 365 * 24 * 60 * 60L,
            userBuf = null
        )
    }

    @Suppress("SameParameterValue")
    private fun genTLSSignature(
        appId: Int,
        secretKey: String,
        userId: String,
        expireTime: Long,
        userBuf: ByteArray?
    ): String {
        val currentTime = System.currentTimeMillis() / 1000
        val sigDoc = JSONObject()
        sigDoc.put("TLS.ver", "2.0")
        sigDoc.put("TLS.identifier", userId)
        sigDoc.put("TLS.sdkappid", appId)
        sigDoc.put("TLS.expire", expireTime)
        sigDoc.put("TLS.time", currentTime)
        var base64UserBuf: String? = null
        if (userBuf != null) {
            base64UserBuf = Base64.encodeToString(userBuf, Base64.NO_WRAP)
            sigDoc.put("TLS.userbuf", base64UserBuf)
        }
        val sig = hmacSHA256(appId, secretKey, userId, currentTime, expireTime, base64UserBuf)
        sigDoc.put("TLS.sig", sig)
        val compressor = Deflater()
        compressor.setInput(sigDoc.toString().toByteArray())
        compressor.finish()
        val compressedBytes = ByteArray(size = 2048)
        val compressedBytesLength = compressor.deflate(compressedBytes)
        compressor.end()
        return String(
            base64EncodeUrl(
                compressedBytes.copyOfRange(
                    fromIndex = 0,
                    toIndex = compressedBytesLength
                )
            )
        )
    }

    private fun hmacSHA256(
        sdkAppId: Int,
        secretKey: String,
        userId: String,
        currentTime: Long,
        expireTime: Long,
        base64UserBuf: String?
    ): String {
        var contentToBeSigned = """
            TLS.identifier:$userId
            TLS.sdkappid:$sdkAppId
            TLS.time:$currentTime
            TLS.expire:$expireTime
            
            """.trimIndent()
        if (base64UserBuf != null) {
            contentToBeSigned += "TLS.userbuf:$base64UserBuf\n"
        }
        val byteKey = secretKey.toByteArray(charset("UTF-8"))
        val hmac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(byteKey, "HmacSHA256")
        hmac.init(keySpec)
        val byteSig = hmac.doFinal(contentToBeSigned.toByteArray(charset("UTF-8")))
        return String(Base64.encode(byteSig, Base64.NO_WRAP))
    }

    private fun base64EncodeUrl(input: ByteArray): ByteArray {
        val base64 = String(Base64.encode(input, Base64.NO_WRAP)).toByteArray()
        for (i in base64.indices) when (base64[i].toInt().toChar()) {
            '+' -> {
                base64[i] = '*'.code.toByte()
            }

            '/' -> {
                base64[i] = '-'.code.toByte()
            }

            '=' -> {
                base64[i] = '_'.code.toByte()
            }

            else -> {

            }
        }
        return base64
    }

}