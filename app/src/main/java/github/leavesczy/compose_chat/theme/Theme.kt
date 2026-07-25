package github.leavesczy.compose_chat.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Density
import github.leavesczy.compose_chat.ui.provider.AppThemeProvider

@Stable
enum class AppThemeMode {
    Light,
    Dark,
    Gray;
}

@Stable
data class AppColor(
    private val day: Color,
    private val night: Color,
    private val darkTheme: Boolean
) {

    val color = if (darkTheme) {
        night
    } else {
        day
    }

}

@Stable
data class AppColorScheme(private val darkTheme: Boolean) {
    /** Primary brand — buttons, FAB, selected tab, links */
    val c_FF5BA3F7_FF60A5FA = AppColor(
        day = Color(color = 0xFF5BA3F7),
        night = Color(color = 0xFF60A5FA),
        darkTheme = darkTheme
    )

    /** Primary text */
    val c_FF0B1F3A_DEFFFFFF = AppColor(
        day = Color(color = 0xFF0B1F3A),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    )

    /** Page / scaffold background — soft blue wash for day */
    val c_FFEAF2FF_FF0B1220 = AppColor(
        day = Color(color = 0xFFEAF2FF),
        night = Color(color = 0xFF0B1220),
        darkTheme = darkTheme
    )

    /** Always white — icons on brand surfaces */
    val c_FFFFFFFF_FFFFFFFF = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    )

    /** Secondary / hint text */
    val c_FF3D5A80_99FFFFFF = AppColor(
        day = Color(color = 0xFF3D5A80),
        night = Color(color = 0x99FFFFFF),
        darkTheme = darkTheme
    )

    /** Drawer / side panel surface — soft blue in day */
    val c_FFE3EDFF_FF0F1726 = AppColor(
        day = Color(color = 0xFFE3EDFF),
        night = Color(color = 0xFF0F1726),
        darkTheme = darkTheme
    )

    /** Cursor / selection accent */
    val c_FF4C9EFF_FF60A5FA = AppColor(
        day = Color(color = 0xFF4C9EFF),
        night = Color(color = 0xFF60A5FA),
        darkTheme = darkTheme
    )

    /** Peer message bubble / soft divider */
    val c_FFD4E6FF_FF243044 = AppColor(
        day = Color(color = 0xFFD4E6FF),
        night = Color(color = 0xFF243044),
        darkTheme = darkTheme
    )

    /** Soft container (peer bubble shell) */
    val c_FFFFFFFF_FF243044 = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF243044),
        darkTheme = darkTheme
    )

    /** Text on light bubbles / input label */
    val c_FF0B1F3A_FFFFFFFF = AppColor(
        day = Color(color = 0xFF0B1F3A),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    )

    /** Handle / muted chrome */
    val c_330B1F3A_B3FFFFFF = AppColor(
        day = Color(color = 0x330B1F3A),
        night = Color(color = 0xB3FFFFFF),
        darkTheme = darkTheme
    )

    /** Card / sheet elevated surface */
    val c_FFFFFFFF_FF162033 = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF162033),
        darkTheme = darkTheme
    )

    /** Modal scrim */
    val c_80000000_99000000 = AppColor(
        day = Color(color = 0x80000000),
        night = Color(color = 0x99000000),
        darkTheme = darkTheme
    )

    /** Immersive dark surface (preview) */
    val c_FF0B1220_FF0B1220 = AppColor(
        day = Color(color = 0xFF0B1220),
        night = Color(color = 0xFF0B1220),
        darkTheme = darkTheme
    )

    /** Danger / error */
    val c_FFFF545C_FFFA525A = AppColor(
        day = Color(color = 0xFFFF545C),
        night = Color(color = 0xFFFA525A),
        darkTheme = darkTheme
    )

    /** Soft blue-gray overlay */
    val c_6684A9D6_6684A9D6 = AppColor(
        day = Color(color = 0x6684A9D6),
        night = Color(color = 0x6684A9D6),
        darkTheme = darkTheme
    )

    /** Lighter blue-gray overlay */
    val c_3384A9D6_3384A9D6 = AppColor(
        day = Color(color = 0x3384A9D6),
        night = Color(color = 0x3384A9D6),
        darkTheme = darkTheme
    )

    /** Bottom bar / secondary panel */
    val c_FFDCEBFF_FF162033 = AppColor(
        day = Color(color = 0xFFDCEBFF),
        night = Color(color = 0xFF162033),
        darkTheme = darkTheme
    )

    /** Image scrim */
    val c_33000000_33000000 = AppColor(
        day = Color(color = 0x33000000),
        night = Color(color = 0x33000000),
        darkTheme = darkTheme
    )
}

private val LocalAppColorScheme = staticCompositionLocalOf<AppColorScheme> {
    error("CompositionLocal LocalAppColorScheme not present")
}

private val LocalAppCursorColor = staticCompositionLocalOf<Brush> {
    error("CompositionLocal LocalAppCursorColor not present")
}

private val LightColorScheme = lightColorScheme(background = Color(color = 0xFFEAF2FF))

private val DarkColorScheme = darkColorScheme(background = Color(color = 0xFF0B1220))

private val LightAppColorScheme = AppColorScheme(darkTheme = false)

private val DarkAppColorScheme = AppColorScheme(darkTheme = true)

object AppTheme {

    val colorScheme: AppColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColorScheme.current

    val cursorColor: Brush
        @Composable
        @ReadOnlyComposable
        get() = LocalAppCursorColor.current

}

@Composable
fun AppTheme(
    appThemeMode: AppThemeMode = AppThemeProvider.appThemeMode,
    content: @Composable () -> Unit
) {
    val localResources = LocalResources.current
    val density = remember {
        Density(
            density = localResources.displayMetrics.widthPixels / 380f,
            fontScale = 1f
        )
    }
    val colorScheme: ColorScheme
    val appColorScheme: AppColorScheme
    when (appThemeMode) {
        AppThemeMode.Light, AppThemeMode.Gray -> {
            colorScheme = LightColorScheme
            appColorScheme = LightAppColorScheme
        }

        AppThemeMode.Dark -> {
            colorScheme = DarkColorScheme
            appColorScheme = DarkAppColorScheme
        }
    }
    val accent = appColorScheme.c_FF4C9EFF_FF60A5FA.color
    val textSelectionColors = remember(key1 = accent) {
        TextSelectionColors(
            handleColor = accent,
            backgroundColor = accent.copy(alpha = 0.4f)
        )
    }
    val cursorBrush = remember(key1 = accent) {
        SolidColor(value = accent)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(
                LocalDensity provides density,
                LocalAppColorScheme provides appColorScheme,
                LocalTextSelectionColors provides textSelectionColors,
                LocalAppCursorColor provides cursorBrush
            ) {
                when (appThemeMode) {
                    AppThemeMode.Light,
                    AppThemeMode.Dark -> {
                        content()
                    }

                    AppThemeMode.Gray -> {
                        val colorMatrix = remember {
                            val colorMatrix = ColorMatrix()
                            colorMatrix.setToSaturation(0f)
                            colorMatrix
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawIntoCanvas { canvas ->
                                        val paint = Paint()
                                        paint.colorFilter = ColorFilter.colorMatrix(colorMatrix)
                                        canvas.saveLayer(bounds = size.toRect(), paint)
                                        drawContent()
                                        canvas.restore()
                                    }
                                },
                            contentAlignment = Alignment.TopCenter
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    )
}
