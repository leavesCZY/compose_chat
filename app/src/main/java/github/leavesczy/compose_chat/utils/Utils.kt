package github.leavesczy.compose_chat.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private val mainHandler by lazy {
    Handler(Looper.getMainLooper())
}

fun showToast(msg: Any?) {
    val message = msg?.toString()
    if (message.isNullOrBlank()) {
        return
    }
    if (Looper.myLooper() == Looper.getMainLooper()) {
        showToast(message = message)
    } else {
        mainHandler.post {
            showToast(message = message)
        }
    }
}

private fun showToast(message: String) {
    Toast.makeText(ContextHolder.context, message, Toast.LENGTH_SHORT).show()
}