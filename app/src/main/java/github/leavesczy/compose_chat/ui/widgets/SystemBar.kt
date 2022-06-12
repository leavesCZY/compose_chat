package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.ui.theme.DarkColorScheme
import github.leavesczy.compose_chat.ui.theme.LightColorScheme

/**
 * @Author: leavesCZY
 * @Date: 2021/10/26 10:49
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun SystemBarColor(appTheme: AppTheme) {
    SystemBarColor(
        statusBarColor = Color.Transparent,
        statusBarDarkIcons = appTheme == AppTheme.Light || appTheme == AppTheme.Gray,
        navigationBarColor = if (appTheme == AppTheme.Dark) {
            DarkColorScheme.background
        } else {
            LightColorScheme.background
        },
        navigationDarkIcons = appTheme != AppTheme.Dark
    )
}

@Composable
private fun SystemBarColor(
    statusBarColor: Color,
    statusBarDarkIcons: Boolean,
    navigationBarColor: Color,
    navigationDarkIcons: Boolean
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = statusBarColor,
        darkIcons = statusBarDarkIcons
    )
    systemUiController.setNavigationBarColor(
        color = navigationBarColor,
        darkIcons = navigationDarkIcons,
        navigationBarContrastEnforced = false
    )
}