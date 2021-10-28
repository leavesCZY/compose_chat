package github.leavesc.compose_chat.ui.weigets

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * @Author: leavesC
 * @Date: 2021/10/26 10:49
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun SetSystemBarsColor(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = MaterialTheme.colors.background,
    isLightTheme: Boolean = MaterialTheme.colors.isLight
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = isLightTheme
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = isLightTheme
        )
        systemUiController.systemBarsDarkContentEnabled = isLightTheme
    }
}