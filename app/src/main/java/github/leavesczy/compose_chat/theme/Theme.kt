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

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
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
    val c_FF42A5F5_FF26A69A = AppColor(
        day = Color(color = 0xFF42A5F5),
        night = Color(color = 0xFF26A69A),
        darkTheme = darkTheme
    )
    val c_FF001018_DEFFFFFF = AppColor(
        day = Color(color = 0xFF001018),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    )
    val c_FFFFFFFF_FF101010 = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF101010),
        darkTheme = darkTheme
    )
    val c_FFFFFFFF_FFFFFFFF = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    )
    val c_FF384F60_99FFFFFF = AppColor(
        day = Color(color = 0xFF384F60),
        night = Color(color = 0x99FFFFFF),
        darkTheme = darkTheme
    )
    val c_FFFFFFFF_FF161616 = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF161616),
        darkTheme = darkTheme
    )
    val c_FF5386E5_FF5386E5 = AppColor(
        day = Color(color = 0xFF5386E5),
        night = Color(color = 0xFF5386E5),
        darkTheme = darkTheme
    )
    val c_FF1BA2E6_FF1BA2E6 = AppColor(
        day = Color(color = 0xFF1BA2E6),
        night = Color(color = 0xFF1BA2E6),
        darkTheme = darkTheme
    )
    val c_FFE2E1EC_FF45464F = AppColor(
        day = Color(color = 0xFFE2E1EC),
        night = Color(color = 0xFF45464F),
        darkTheme = darkTheme
    )
    val c_FFFFFFFF_FF45464F = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF45464F),
        darkTheme = darkTheme
    )
    val c_FF3A3D4D_FFFFFFFF = AppColor(
        day = Color(color = 0xFF3A3D4D),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    )
    val c_333A3D4D_B3FFFFFF = AppColor(
        day = Color(color = 0x333A3D4D),
        night = Color(color = 0xB3FFFFFF),
        darkTheme = darkTheme
    )
    val c_FFFFFFFF_FF22202A = AppColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF22202A),
        darkTheme = darkTheme
    )
    val c_80000000_99000000 = AppColor(
        day = Color(color = 0x80000000),
        night = Color(color = 0x99000000),
        darkTheme = darkTheme
    )
    val c_FF1C1B1F_FFFFFFFF = AppColor(
        day = Color(color = 0xFF1C1B1F),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    )
    val c_FF22202A_FF22202A = AppColor(
        day = Color(color = 0xFF22202A),
        night = Color(color = 0xFF22202A),
        darkTheme = darkTheme
    )
    val c_FFFF545C_FFFA525A = AppColor(
        day = Color(color = 0xFFFF545C),
        night = Color(color = 0xFFFA525A),
        darkTheme = darkTheme
    )
    val c_66CCCCCC_66CCCCCC = AppColor(
        day = Color(color = 0x66CCCCCC),
        night = Color(color = 0x66CCCCCC),
        darkTheme = darkTheme
    )
    val c_33CCCCCC_33CCCCCC = AppColor(
        day = Color(color = 0x33CCCCCC),
        night = Color(color = 0x33CCCCCC),
        darkTheme = darkTheme
    )
    val c_FFEFF1F3_FF22202A = AppColor(
        day = Color(color = 0xFFEFF1F3),
        night = Color(color = 0xFF22202A),
        darkTheme = darkTheme
    )
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

private val LightColorScheme = lightColorScheme(background = Color(color = 0xFFFFFFFF))

private val DarkColorScheme = darkColorScheme(background = Color(color = 0xFF101010))

private val LightAppColorScheme = AppColorScheme(darkTheme = false)

private val DarkAppColorScheme = AppColorScheme(darkTheme = true)

private val customTextSelectionColors = TextSelectionColors(
    handleColor = Color(color = 0xFF1BA2E6),
    backgroundColor = Color(color = 0x661BA2E6)
)

private val customCursorColor = SolidColor(value = Color(color = 0xFF1BA2E6))

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
    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(
                LocalDensity provides density,
                LocalAppColorScheme provides appColorScheme,
                LocalTextSelectionColors provides customTextSelectionColors,
                LocalAppCursorColor provides customCursorColor
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