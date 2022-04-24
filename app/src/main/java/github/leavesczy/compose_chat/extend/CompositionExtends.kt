package github.leavesczy.compose_chat.extend

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

/**
 * @Author: leavesCZY
 * @Date: 2021/10/21 11:57
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
val LocalNavHostController = staticCompositionLocalOf<NavHostController> {
    error("CompositionLocal NavHostController not present")
}

@Composable
fun ProvideNavHostController(
    navHostController: NavHostController,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalNavHostController provides navHostController) {
        content()
    }
}