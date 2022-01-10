package github.leavesc.compose_chat.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.model.AppTheme

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColorLight,
    primaryVariant = PrimaryVariantColorLight,
    secondary = SecondaryColorLight,
    secondaryVariant = SecondaryVariantColorLight,
    background = BackgroundColorLight,
    surface = SurfaceColorLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    onPrimary = OnPrimaryColorDark,
    primaryContainer = PrimaryContainerColorDark,
    surface = SurfaceColorDark,
    onSurface = OnSurfaceColorDark,
    background = BackgroundColorDark,
    onSecondaryContainer = onSecondaryContainerDark
)

private val BlueColorScheme = lightColorScheme(
    primary = PrimaryColorBlue,
    onPrimary = OnPrimaryColorBlue,
    primaryContainer = PrimaryContainerColorBlue,
    surface = SurfaceColorBlue,
    onSurface = OnSurfaceColorBlue,
    background = BackgroundColorBlue,
    onSecondaryContainer = onSecondaryContainerBlue
)

@Composable
fun ChatTheme(
    appTheme: AppTheme = AppThemeCache.currentTheme,
    content: @Composable () -> Unit
) {
    val colors = when (appTheme) {
        AppTheme.Light -> {
            LightColorScheme
        }
        AppTheme.Blue -> {
            BlueColorScheme
        }
        AppTheme.Gray -> {
            LightColorScheme
        }
    }
    val typography = when (appTheme) {
        AppTheme.Light -> {
            LightTypography
        }
        AppTheme.Dark -> {
            DarkTypography
        }
        AppTheme.Blue -> {
            BlueTypography
        }
        AppTheme.Gray -> {
            LightTypography
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}