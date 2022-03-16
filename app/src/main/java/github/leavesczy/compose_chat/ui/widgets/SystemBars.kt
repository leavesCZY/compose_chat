package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * @Author: leavesCZY
 * @Date: 2021/10/26 10:49
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun SetSystemBarsColor(
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