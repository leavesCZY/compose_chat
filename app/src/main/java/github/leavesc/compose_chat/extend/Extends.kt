package github.leavesc.compose_chat.extend

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import github.leavesc.compose_chat.model.Screen

/**
 * @Author: leavesC
 * @Date: 2021/7/26 0:28
 * @Desc:
 * @Github：https://github.com/leavesC
 */
fun NavController.navigate(screen: Screen) {
    navigate(route = screen.route)
}

fun NavController.navigateWithBack(currentScreen: Screen, targetScreen: Screen) {
    navigate(route = targetScreen.route) {
        popUpTo(route = currentScreen.route) {
            inclusive = true
        }
    }
}

fun NavBackStackEntry.getArgument(key: String): String {
    return arguments?.getString(key)
        ?: throw IllegalArgumentException()
}

val LocalInputMethodManager = staticCompositionLocalOf<InputMethodManager> {
    error("CompositionLocal InputMethodManager not present")
}

@Composable
fun ProvideInputMethodManager(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val inputMethodManager = remember {
        context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
    }
    CompositionLocalProvider(LocalInputMethodManager provides inputMethodManager) {
        content()
    }
}