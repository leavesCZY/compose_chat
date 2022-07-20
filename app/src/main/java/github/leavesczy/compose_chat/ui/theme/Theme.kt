package github.leavesczy.compose_chat.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.ui.widgets.SystemBarColor

val LightColorScheme = lightColorScheme(
    primary = PrimaryColorLight,
    onPrimary = OnPrimaryColorLight,
    primaryContainer = PrimaryContainerColorLight,
    surface = SurfaceColorLight,
    onSurface = OnSurfaceColorLight,
    background = BackgroundColorLight,
    onSecondaryContainer = onSecondaryContainerLight
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    onPrimary = OnPrimaryColorDark,
    primaryContainer = PrimaryContainerColorDark,
    surface = SurfaceColorDark,
    onSurface = OnSurfaceColorDark,
    background = BackgroundColorDark,
    onSecondaryContainer = onSecondaryContainerDark
)

@Composable
fun ComposeChatTheme(
    appTheme: AppTheme = AppThemeCache.currentTheme,
    content: @Composable () -> Unit
) {
    SystemBarColor(appTheme = appTheme)
    val colors = when (appTheme) {
        AppTheme.Light -> {
            LightColorScheme
        }
        AppTheme.Dark -> {
            DarkColorScheme
        }
        AppTheme.Gray -> {
            LightColorScheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = {
            content()
            if (appTheme == AppTheme.Gray) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.LightGray,
                        blendMode = BlendMode.Saturation
                    )
                }
            }
        }
    )
}