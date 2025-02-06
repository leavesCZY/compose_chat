package github.leavesczy.compose_chat.provider

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import github.leavesczy.compose_chat.R

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
        val toast = buildCustomToast(context = context, msg = msg)
        toast.show()
    }

    private fun buildCustomToast(context: Context, msg: String): Toast {
        val layout =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.layout_toast,
                null
            )
        val text = layout.findViewById<TextView>(R.id.tvToastMessage)
        text.text = msg
        val toast = Toast(context)
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        return toast
    }

}