package github.leavesczy.compose_chat.proxy.utils

import com.tencent.imsdk.v2.V2TIMManager
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2021/7/7 17:17
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object RandomUtils {

    private val randomInt: Int
        get() = Random.nextInt()

    private val randomLong: Long
        get() = Random.nextLong()

    fun generateMessageId(): String {
        return (System.currentTimeMillis() + randomLong + randomLong).toString()
    }

    fun generateMessageTimestamp(): Long {
        return V2TIMManager.getInstance().serverTime
    }

}