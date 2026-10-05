package github.leavesczy.compose_chat.provider

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import github.leavesczy.compose_chat.base.utils.ContextProvider
import github.leavesczy.compose_chat.base.utils.StringResources

object ToastProvider {

    fun showToast(context: Context = ContextProvider.get(), @StringRes resId: Int) {
        val msg = StringResources.getString(resId = resId)
        showToast(context = context, msg = msg)
    }

    fun showToast(context: Context = ContextProvider.get(), msg: String?) {
        if (msg.isNullOrBlank()) {
            return
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}
