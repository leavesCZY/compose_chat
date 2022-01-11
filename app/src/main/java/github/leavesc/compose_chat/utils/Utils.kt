package github.leavesc.compose_chat.utils

import android.util.Log
import android.widget.Toast

/**
 * @Author: leavesC
 * @Date: 2021/6/20 21:52
 * @Desc:
 * @Github：https://github.com/leavesC
 */
fun showToast(msg: Any?, lengthLong: Boolean = false) {
    Toast.makeText(
        ContextHolder.context, msg.toString(), if (lengthLong) {
            Toast.LENGTH_LONG
        } else {
            Toast.LENGTH_SHORT
        }
    ).show()
}

fun log(tag: String = "TAG", log: () -> Any?) {
    Log.e(tag, log()?.toString() ?: "empty")
}