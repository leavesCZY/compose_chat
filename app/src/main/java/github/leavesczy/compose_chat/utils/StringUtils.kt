package github.leavesczy.compose_chat.utils

import okhttp3.internal.and

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 18:12
 * @Desc:
 */
object StringUtils {

    private const val chars = "0123456789ABCDEF"

    private val charsArray = chars.toCharArray()

    fun str2HexStr(str: String): String {
        val sb = StringBuilder("")
        val bytes = str.toByteArray()
        var bit: Int
        for (i in bytes.indices) {
            bit = bytes[i] and 0x0f0 shr 4
            sb.append(charsArray[bit])
            bit = bytes[i] and 0x0f
            sb.append(charsArray[bit])
        }
        return sb.toString().trim { it <= ' ' }
    }

    fun hexStr2Str(hexStr: String): String {
        val hex = hexStr.toCharArray()
        val bytes = ByteArray(hexStr.length / 2)
        var i: Int
        for (index in bytes.indices) {
            i = chars.indexOf(hex[2 * index]) * 16
            i += chars.indexOf(hex[2 * index + 1])
            bytes[index] = (i and 0xff).toByte()
        }
        return String(bytes)
    }

}