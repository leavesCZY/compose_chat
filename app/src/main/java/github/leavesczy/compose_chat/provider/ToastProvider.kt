package github.leavesczy.compose_chat.provider

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
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

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    private fun hookToastIfNeed(toast: Toast) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            val cToast = Toast::class.java
            val fTn = cToast.getDeclaredField("mTN")
            fTn.isAccessible = true
            val oTn = fTn.get(toast)
            val cTn = oTn.javaClass
            val fHandle = cTn.getDeclaredField("mHandler")
            fHandle.isAccessible = true
            fHandle.set(oTn, ProxyHandler(fHandle.get(oTn) as Handler))
        }
    }

    private class ProxyHandler(private val mHandler: Handler) : Handler(mHandler.looper) {

        override fun handleMessage(msg: Message) {
            try {
                mHandler.handleMessage(msg)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    }

}