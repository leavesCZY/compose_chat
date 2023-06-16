package github.leavesczy.compose_chat.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.provider.ToastProvider

open class BaseViewModel : ViewModel() {

    protected val context: Context
        get() = ContextProvider.context

    protected fun showToast(msg: String) {
        ToastProvider.showToast(msg = msg)
    }

}