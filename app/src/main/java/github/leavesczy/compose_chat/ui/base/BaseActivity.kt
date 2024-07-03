package github.leavesczy.compose_chat.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
open class BaseActivity : AppCompatActivity() {

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
                ComposeChatTheme {
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
    protected fun SetSystemBarUi(appTheme: AppTheme) {
        val context = LocalContext.current
        LaunchedEffect(key1 = appTheme == AppTheme.Dark) {
            if (context is Activity) {
                val systemBarsDarkIcon = when (appTheme) {
                    AppTheme.Light, AppTheme.Gray -> {
                        true
                    }

                    AppTheme.Dark -> {
                        false
                    }
                }
                val window = context.window
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    isAppearanceLightStatusBars = systemBarsDarkIcon
                    isAppearanceLightNavigationBars = systemBarsDarkIcon
                }
            }
        }
    }

    protected fun showToast(msg: String) {
        ToastProvider.showToast(context = this, msg = msg)
    }

    protected inline fun <reified T : Activity> startActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }

}