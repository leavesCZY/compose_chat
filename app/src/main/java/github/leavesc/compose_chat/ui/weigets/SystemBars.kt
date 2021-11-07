package github.leavesc.compose_chat.ui.weigets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.derivedWindowInsetsTypeOf
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * @Author: leavesC
 * @Date: 2021/10/26 10:49
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun SetSystemBarsColor(
    statusBarColor: Color,
    navigationBarColor: Color,
    statusBarDarkIcons: Boolean,
    navigationDarkIcons: Boolean
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = statusBarColor,
        darkIcons = statusBarDarkIcons
    )
    systemUiController.setNavigationBarColor(
        color = navigationBarColor,
        darkIcons = navigationDarkIcons
    )
}

@Composable
fun navigationBarsWithImePadding(): PaddingValues {
    val ime = LocalWindowInsets.current.ime
    val navBars = LocalWindowInsets.current.navigationBars
    val insets = remember(key1 = ime, key2 = navBars) {
        derivedWindowInsetsTypeOf(ime, navBars)
    }
    return rememberInsetsPaddingValues(
        insets = insets,
        applyStart = true,
        applyEnd = true,
        applyBottom = true
    )
}