package github.leavesczy.compose_chat.provider

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import github.leavesczy.compose_chat.base.utils.ContextProvider
import github.leavesczy.compose_chat.base.utils.StringResources

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
object ToastProvider {

    fun showToast(context: Context = ContextProvider.get(), @StringRes resId: Int) {
        val msg = StringResources.getString(resId = resId)
        showToast(context = context, msg = msg)
    }

    fun showToast(context: Context = ContextProvider.get(), msg: String?) {
        if (msg.isNullOrBlank()) {
            return
        }
        val toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}