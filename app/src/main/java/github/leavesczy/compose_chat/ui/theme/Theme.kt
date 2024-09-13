package github.leavesczy.compose_chat.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private val primaryColorLight = Color(0xFF42A5F5)
private val inversePrimaryColorLight = Color(0XFF5775A8)
private val inverseSurfaceColorLight = Color(0xFFE2E1EC)
private val inverseOnSurfaceColorLight = Color(0xFF3A3D4D)
private val onPrimaryColorLight = Color(0xFF22202A)
private val primaryContainerColorLight = Color(0xFFFFFFFF)
private val surfaceColorLight = Color(0xFFFFFFFF)
private val onSurfaceColorLight = Color(0xFF1C1B1F)
private val backgroundColorLight = Color(0xFFFFFFFF)
private val onSecondaryContainerLight = Color(0xFFF4F4F4)

private val primaryColorDark = Color(0xFF26A69A)
private val inversePrimaryColorDark = Color(0xFF45464F)
private val inverseSurfaceColorDark = Color(0xFF45464F)
private val inverseOnSurfaceColorDark = Color(0xFFFFFFFF)
private val onPrimaryColorDark = Color(0xFFFFFFFF)
private val primaryContainerColorDark = Color(0xFF3A3D4D)
private val surfaceColorDark = Color(0xFF22202A)
private val onSurfaceColorDark = Color(0xFFFFFFFF)
private val backgroundColorDark = Color(0xFF22202A)
private val onSecondaryContainerDark = Color(0xFF3A3D4D)

private val lightColorScheme = lightColorScheme(
    primary = primaryColorLight,
    inversePrimary = inversePrimaryColorLight,
    onPrimary = onPrimaryColorLight,
    primaryContainer = primaryContainerColorLight,
    surface = surfaceColorLight,
    onSurface = onSurfaceColorLight,
    inverseSurface = inverseSurfaceColorLight,
    inverseOnSurface = inverseOnSurfaceColorLight,
    background = backgroundColorLight,
    onSecondaryContainer = onSecondaryContainerLight
)

private val darkColorScheme = darkColorScheme(
    primary = primaryColorDark,
    inversePrimary = inversePrimaryColorDark,
    onPrimary = onPrimaryColorDark,
    primaryContainer = primaryContainerColorDark,
    surface = surfaceColorDark,
    onSurface = onSurfaceColorDark,
    inverseSurface = inverseSurfaceColorDark,
    inverseOnSurface = inverseOnSurfaceColorDark,
    background = backgroundColorDark,
    onSecondaryContainer = onSecondaryContainerDark
)

private val typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 21.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    )
)

val WindowInsetsEmpty = WindowInsets(left = 0.dp, right = 0.dp, top = 0.dp, bottom = 0.dp)

@Composable
fun ComposeChatTheme(content: @Composable () -> Unit) {
    val colorScheme = when (AppThemeProvider.appTheme) {
        AppTheme.Light, AppTheme.Gray -> {
            lightColorScheme
        }

        AppTheme.Dark -> {
            darkColorScheme
        }
    }
    val context = LocalContext.current
    val rememberedDensity = remember {
        Density(
            density = context.resources.displayMetrics.widthPixels / 380f,
            fontScale = 1f
        )
    }
    CompositionLocalProvider(LocalDensity provides rememberedDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    content()
                    if (AppThemeProvider.appTheme == AppTheme.Gray) {
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
        )
    }
}