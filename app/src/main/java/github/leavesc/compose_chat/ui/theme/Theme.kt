package github.leavesc.compose_chat.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.model.AppTheme

private val LightColorPalette = lightColors(
    background = BackgroundColorLight,
    primary = PrimaryColorLight,
    primaryVariant = PrimaryVariantColorLight,
    surface = SurfaceColorLight,
    secondary = SecondaryColorLight,
)

private val DarkColorPalette = darkColors(
    background = BackgroundColorDark,
    primary = PrimaryColorDark,
    primaryVariant = PrimaryVariantColorDark,
    surface = SurfaceColorDark,
    secondary = SecondaryColorDark,
)

private val BlueColorPalette = lightColors(
    background = BackgroundColorBlue,
    primary = PrimaryColorBlue,
    primaryVariant = PrimaryVariantColorBlue,
    surface = SurfaceColorBlue,
    secondary = SecondaryColorBlue,
)

private val PinkColorPalette = lightColors(
    background = BackgroundColorPink,
    primary = PrimaryColorPink,
    primaryVariant = PrimaryVariantColorPink,
    surface = SurfaceColorPink,
    secondary = SecondaryColorPink,
)

@Composable
fun ChatTheme(
    appTheme: AppTheme = AppThemeCache.currentTheme,
    content: @Composable () -> Unit
) {
    val colors = when (appTheme) {
        AppTheme.Light -> {
            LightColorPalette
        }
        AppTheme.Dark -> {
            DarkColorPalette
        }
        AppTheme.Blue -> {
            BlueColorPalette
        }
        AppTheme.Pink -> {
            PinkColorPalette
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
        AppTheme.Pink -> {
            PinkTypography
        }
    }
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = AppShapes,
        content = content
    )
}