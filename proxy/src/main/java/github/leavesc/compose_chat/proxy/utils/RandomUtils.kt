package github.leavesc.compose_chat.proxy.utils

import com.tencent.imsdk.v2.V2TIMManager
import kotlin.random.Random

/**
 * @Author: leavesC
 * @Date: 2021/7/7 17:17
 * @Desc:
 * @Github：https://github.com/leavesC
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