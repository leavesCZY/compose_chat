package github.leavesczy.compose_chat.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import github.leavesczy.compose_chat.base.utils.ContextProvider
import github.leavesczy.compose_chat.base.utils.StringResources
import github.leavesczy.compose_chat.ui.provider.ToastProvider
import github.leavesczy.compose_chat.widgets.LoadingDialogViewState

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
abstract class BaseViewModel : ViewModel() {

    protected val context: Context
        get() = ContextProvider.get()

    val loadingDialogViewState = LoadingDialogViewState()

    protected fun showLoadingDialog(isCancelable: Boolean = false) {
        loadingDialogViewState.show(isCancelable = isCancelable)
    }

    protected fun dismissLoadingDialog() {
        loadingDialogViewState.dismiss()
    }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return StringResources.getString(resId = resId, *formatArgs)
    }

    protected fun showToast(@StringRes resId: Int) {
        ToastProvider.showToast(context = context, resId = resId)
    }

    protected fun showToast(msg: String?) {
        ToastProvider.showToast(context = context, msg = msg)
    }

    protected inline fun <reified T : Activity> Context.startActivity() {
        val intent = Intent(this, T::class.java)
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

}