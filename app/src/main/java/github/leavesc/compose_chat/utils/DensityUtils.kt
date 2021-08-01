package github.leavesc.compose_chat.utils

import android.content.Context

/**
 * @Author: leavesC
 * @Date: 2021/7/8 14:29
 * @Desc:
 * @Github：https://github.com/leavesC
 */
object DensityUtils {

    fun px2dp(context: Context, pxValue: Number): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue.toDouble() / scale + 0.5f).toInt()
    }

}