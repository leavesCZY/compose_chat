package github.leavesc.compose_chat.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private const val smallAlpha = 0.9f
private const val mediumAlpha = 0.6f
private const val largeAlpha = 0.4f

val LightTypography = Typography(
    subtitle1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = Color.Black.copy(alpha = smallAlpha),
    ),
    subtitle2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = Color.Black.copy(alpha = smallAlpha),
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = Color.Black.copy(alpha = smallAlpha),
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = Color.Black.copy(alpha = mediumAlpha),
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = Color.White.copy(alpha = smallAlpha),
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = Color.Black.copy(alpha = smallAlpha),
    )
)

val DarkTypography = LightTypography.copy(
    subtitle1 = LightTypography.subtitle1.copy(color = Color.White.copy(alpha = smallAlpha)),
    subtitle2 = LightTypography.subtitle2.copy(color = Color.White.copy(alpha = smallAlpha)),
    body1 = LightTypography.body1.copy(color = Color.White.copy(alpha = smallAlpha)),
    body2 = LightTypography.body2.copy(color = Color.White.copy(alpha = mediumAlpha)),
    caption = LightTypography.caption.copy(color = Color.White.copy(alpha = smallAlpha)),
)

val BlueTypography = LightTypography.copy(
    body1 = LightTypography.body1.copy(color = Color.Black.copy(alpha = smallAlpha)),
)

val PinkTypography = LightTypography.copy(
    body1 = LightTypography.body1.copy(color = Color.Black.copy(alpha = smallAlpha)),
)