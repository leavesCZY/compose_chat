package github.leavesc.compose_chat.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private const val smallAlpha = 0.95f
private const val mediumAlpha = 0.8f
private const val largeAlpha = 0.4f

val LightTypography = Typography(
    subtitle1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = Color.Black,
    ),
    subtitle2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 1.sp,
        color = Color.Black,
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 1.sp,
        color = Color.Black,
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 1.sp,
        color = Color.Gray,
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = Color.White,
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = Color.Black,
    )
)

val DarkTypography = LightTypography.copy(
    subtitle1 = LightTypography.subtitle1.copy(color = Color.White),
    subtitle2 = LightTypography.subtitle2.copy(color = Color.White),
    body1 = LightTypography.body1.copy(color = Color.White.copy(alpha = mediumAlpha)),
    body2 = LightTypography.body2.copy(color = Color.White.copy(alpha = mediumAlpha)),
    caption = LightTypography.caption.copy(color = Color.White),
)

val BlueTypography = LightTypography.copy(
    body1 = LightTypography.body1.copy(color = Color.Black),
)

val PinkTypography = LightTypography.copy(
    body1 = LightTypography.body1.copy(color = Color.Black),
)