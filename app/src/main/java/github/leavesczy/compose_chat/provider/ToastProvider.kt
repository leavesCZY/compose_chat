package github.leavesczy.compose_chat.provider

import android.content.Context
import android.widget.Toast

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object ToastProvider {

    fun showToast(context: Context, msg: String?) {
        if (msg.isNullOrBlank()) {
            return
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}