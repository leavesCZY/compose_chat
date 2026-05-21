package github.leavesczy.compose_chat.ui.base

import android.os.Bundle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme
import github.leavesczy.compose_chat.ui.theme.AppMaterialTheme

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
abstract class BaseActivity : AppCompatActivity() {

    protected inline fun <reified T : ViewModel> viewModelsInstance(crossinline create: () -> T): Lazy<T> {
        return viewModels(factoryProducer = {
            object : ViewModelProvider.NewInstanceFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return create() as T
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
    }

    protected fun setContent(
        systemBarUi: @Composable () -> Unit = {
            SetSystemBarUi()
        },
        content: @Composable () -> Unit
    ) {
        setContent(
            parent = null,
            content = {
                AppMaterialTheme {
                    systemBarUi()
                    content()
                }
            }
        )
    }

    @Composable
    protected open fun SetSystemBarUi() {
        SetSystemBarUi(appTheme = AppThemeProvider.appTheme)
    }

    @Composable
    private fun SetSystemBarUi(appTheme: AppTheme) {
        val localActivity = LocalActivity.current
        LaunchedEffect(key1 = appTheme == AppTheme.Dark) {
            val systemBarsDarkIcon = when (appTheme) {
                AppTheme.Light, AppTheme.Gray -> {
                    true
                }

                AppTheme.Dark -> {
                    false
                }
            }
            val window = localActivity!!.window
            WindowInsetsControllerCompat(window, window.decorView).apply {
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                isAppearanceLightStatusBars = systemBarsDarkIcon
                isAppearanceLightNavigationBars = systemBarsDarkIcon
            }
        }
    }

    protected fun showToast(@StringRes resId: Int) {
        ToastProvider.showToast(context = this, resId = resId)
    }

}