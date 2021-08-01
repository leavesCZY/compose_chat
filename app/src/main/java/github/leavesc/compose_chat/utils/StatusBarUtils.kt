package github.leavesc.compose_chat.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat

/**
 * @Author: leavesC
 * @Date: 2021/6/4 22:26
 * @Desc:
 * @Github：https://github.com/leavesC
 */
object StatusBarUtil {

    private var statusBarHeightCache = -1

    val statusBarHeightDp: Int
        get() {
            val context = ContextHolder.context
            val statusHeight = getStatusBarHeight(context)
            if (statusHeight > 0) {
                return DensityUtils.px2dp(context, statusHeight)
            }
            return -1
        }

    private fun getStatusBarHeight(context: Context): Int {
        if (statusBarHeightCache > 0) {
            return statusBarHeightCache
        }
        var statusBarHeight = -1
        try {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        } catch (e: Throwable) {
            e.printStackTrace()
            try {
                val clazz = Class.forName("com.android.internal.R\$dimen")
                val `object` = clazz.newInstance()
                val height = clazz.getField("status_bar_height")[`object`].toString().toInt()
                statusBarHeight = context.resources.getDimensionPixelSize(height)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        statusBarHeightCache = statusBarHeight
        return statusBarHeight
    }

    fun setFullscreen(activity: Activity, dark: Boolean) {
        setStatusBarState(
            activity = activity,
            fullscreen = true,
            darkTheme = dark,
            statusColor = Color.TRANSPARENT
        )
    }

    fun setStatusBarLightTheme(activity: Activity, statusColor: Int = Color.parseColor("#FFFFFF")) {
        setStatusBarState(
            activity = activity,
            fullscreen = false,
            darkTheme = true,
            statusColor = statusColor
        )
    }

    fun setStatusBarDarkTheme(
        activity: Activity,
        statusColor: Int = Color.parseColor("#FF222222")
    ) {
        setStatusBarState(
            activity = activity,
            fullscreen = false,
            darkTheme = false,
            statusColor = statusColor
        )
    }

    private fun setStatusBarState(
        activity: Activity,
        fullscreen: Boolean,
        darkTheme: Boolean,
        statusColor: Int
    ) {
        with(activity.window) {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (fullscreen) {
                statusBarColor = Color.TRANSPARENT
                if (darkTheme) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_VISIBLE or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            } else {
                statusBarColor = statusColor
                if (darkTheme) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }
            val childView = findViewById<ViewGroup?>(Window.ID_ANDROID_CONTENT)?.getChildAt(0)
            if (childView != null) {
                childView.fitsSystemWindows = false
                ViewCompat.requestApplyInsets(childView)
            }
        }
    }

}