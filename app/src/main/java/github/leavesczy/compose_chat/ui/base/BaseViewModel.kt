package github.leavesczy.compose_chat.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.provider.ToastProvider

open class BaseViewModel : ViewModel() {

    protected val context: Application
        get() = ContextProvider.context

    protected fun showToast(msg: String) {
        ToastProvider.showToast(context = context, msg = msg)
    }

}