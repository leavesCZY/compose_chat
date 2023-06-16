package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun SystemBarTheme(
    appTheme: AppTheme = AppThemeProvider.appTheme,
    statusBarColor: Color = Color.Transparent,
    statusBarDarkIcons: Boolean = appTheme == AppTheme.Light || appTheme == AppTheme.Gray,
    navigationBarColor: Color = MaterialTheme.colorScheme.background,
    navigationDarkIcons: Boolean = appTheme != AppTheme.Dark
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