package github.leavesczy.compose_chat.base

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
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.theme.AppThemeMode
import github.leavesczy.compose_chat.ui.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.provider.ToastProvider

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
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
                AppTheme {
                    systemBarUi()
                    content()
                }
            }
        )
    }

    @Composable
    protected open fun SetSystemBarUi() {
        SetSystemBarUi(appThemeMode = AppThemeProvider.appThemeMode)
    }

    @Composable
    private fun SetSystemBarUi(appThemeMode: AppThemeMode) {
        val localActivity = LocalActivity.current
        LaunchedEffect(key1 = appThemeMode == AppThemeMode.Dark, key2 = localActivity) {
            val activity = localActivity ?: return@LaunchedEffect
            val systemBarsDarkIcon = when (appThemeMode) {
                AppThemeMode.Light, AppThemeMode.Gray -> true
                AppThemeMode.Dark -> false
            }
            val window = activity.window
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