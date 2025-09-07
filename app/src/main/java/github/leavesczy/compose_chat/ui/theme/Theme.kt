package github.leavesczy.compose_chat.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private val lightColorScheme = lightColorScheme(
    background = Color(0xFFFFFFFF)
)

private val darkColorScheme = darkColorScheme(
    background = Color(0xFF101010)
)

object ComposeChatTheme {

    val colorScheme: ComposeChatColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalComposeChatColorScheme.current

}

val WindowInsetsEmpty = WindowInsets(left = 0.dp, right = 0.dp, top = 0.dp, bottom = 0.dp)

@Composable
fun ComposeChatTheme(content: @Composable () -> Unit) {
    val localResources = LocalResources.current
    val density = remember {
        Density(
            density = localResources.displayMetrics.widthPixels / 375.0f, fontScale = 1f
        )
    }
    val colorScheme: ColorScheme
    val composeChatColorScheme: ComposeChatColorScheme
    when (AppThemeProvider.appTheme) {
        AppTheme.Light, AppTheme.Gray -> {
            colorScheme = lightColorScheme
            composeChatColorScheme = LightComposeChatColorScheme
        }

        AppTheme.Dark -> {
            colorScheme = darkColorScheme
            composeChatColorScheme = DarkComposeChatColorScheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(
                LocalDensity provides density,
                LocalComposeChatColorScheme provides composeChatColorScheme,
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        content()
                        when (AppThemeProvider.appTheme) {
                            AppTheme.Light,
                            AppTheme.Dark -> {

                            }

                            AppTheme.Gray -> {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    drawRect(
                                        color = Color.Black,
                                        blendMode = BlendMode.Saturation
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}