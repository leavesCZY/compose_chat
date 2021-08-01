package github.leavesc.compose_chat.proxy.utils

import android.util.Base64
import github.leavesc.compose_chat.proxy.consts.AppConst
import org.json.JSONObject
import java.util.zip.Deflater
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @Author: leavesC
 * @Date: 2021/6/18 23:44
 * @Desc:
 * @Github：https://github.com/leavesC
 */
internal object GenerateUserSig {

    private const val SDK_APP_ID = AppConst.APP_ID.toLong()

    private const val SECRET_KEY = AppConst.APP_SECRET_KEY

    private const val EXPIRE_TIME = 30 * 24 * 60 * 60L

    fun genUserSig(userId: String): String {
        return genTLSSignature(SDK_APP_ID, userId, EXPIRE_TIME, null, SECRET_KEY)
    }

    private fun genTLSSignature(
        sdkAppId: Long,
        userId: String,
        expire: Long,
        userbuf: ByteArray?,
        priKeyContent: String
    ): String {
        val currTime = System.currentTimeMillis() / 1000
        val sigDoc = JSONObject()
        sigDoc.put("TLS.ver", "2.0")
        sigDoc.put("TLS.identifier", userId)
        sigDoc.put("TLS.sdkappid", sdkAppId)
        sigDoc.put("TLS.expire", expire)
        sigDoc.put("TLS.time", currTime)
        var base64UserBuf: String? = null
        if (null != userbuf) {
            base64UserBuf = Base64.encodeToString(userbuf, Base64.NO_WRAP)
            sigDoc.put("TLS.userbuf", base64UserBuf)
        }
        val sig = hmacsha256(sdkAppId, userId, currTime, expire, priKeyContent, base64UserBuf)
        sigDoc.put("TLS.sig", sig)
        val compressor = Deflater()
        compressor.setInput(sigDoc.toString().toByteArray())
        compressor.finish()
        val compressedBytes = ByteArray(2048)
        val compressedBytesLength: Int = compressor.deflate(compressedBytes)
        compressor.end()
        return String(base64EncodeUrl(compressedBytes.copyOfRange(0, compressedBytesLength)))
    }

    private fun hmacsha256(
        sdkAppId: Long,
        userId: String,
        currTime: Long,
        expire: Long,
        priKeyContent: String,
        base64Userbuf: String?
    ): String {
        var contentToBeSigned = """
            TLS.identifier:$userId
            TLS.sdkappid:$sdkAppId
            TLS.time:$currTime
            TLS.expire:$expire
            
            """.trimIndent()
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:$base64Userbuf\n"
        }
        val byteKey = priKeyContent.toByteArray(charset("UTF-8"))
        val hmac: Mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(byteKey, "HmacSHA256")
        hmac.init(keySpec)
        val byteSig: ByteArray = hmac.doFinal(contentToBeSigned.toByteArray(charset("UTF-8")))
        return String(Base64.encode(byteSig, Base64.NO_WRAP))
    }

    private fun base64EncodeUrl(input: ByteArray): ByteArray {
        val base64: ByteArray = String(Base64.encode(input, Base64.NO_WRAP)).toByteArray()
        for (i in base64.indices)
            when (base64[i].toInt().toChar()) {
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