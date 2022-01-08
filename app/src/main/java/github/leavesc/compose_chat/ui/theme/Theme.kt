package github.leavesc.compose_chat.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.model.AppTheme

private val LightColorPalette = lightColors(
    primary = PrimaryColorLight,
    primaryVariant = PrimaryVariantColorLight,
    secondary = SecondaryColorLight,
    secondaryVariant = SecondaryVariantColorLight,
    background = BackgroundColorLight,
    surface = SurfaceColorLight,
)

private val DarkColorPalette = darkColors(
    primary = PrimaryColorDark,
    primaryVariant = PrimaryVariantColorDark,
    secondary = SecondaryColorDark,
    secondaryVariant = SecondaryVariantColorDark,
    background = BackgroundColorDark,
    surface = SurfaceColorDark,
)

private val BlueColorPalette = lightColors(
    primary = PrimaryColorBlue,
    primaryVariant = PrimaryVariantColorBlue,
    secondary = SecondaryColorBlue,
    secondaryVariant = SecondaryVariantColorBlue,
    background = BackgroundColorBlue,
    surface = SurfaceColorBlue,
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
        AppTheme.Gray -> {
            LightColorPalette
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
        colors = colors,
        typography = typography,
        shapes = AppShapes,
        content = content
    )
}