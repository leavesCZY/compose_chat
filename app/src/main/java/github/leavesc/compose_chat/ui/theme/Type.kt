package github.leavesc.compose_chat.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LightTypography = Typography(
    subtitle1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.Black.copy(alpha = 0.8f),
    ),
    subtitle2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.Black.copy(alpha = 0.5f),
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.Black.copy(alpha = 0.5f),
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.Black.copy(alpha = 0.5f),
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 1f),
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 19.sp,
        color = Color.Black.copy(alpha = 0.8f),
    )
)

val DarkTypography = LightTypography.copy(
    subtitle1 = LightTypography.subtitle1.copy(color = Color.White.copy(alpha = 0.8f)),
    subtitle2 = LightTypography.subtitle2.copy(color = Color.White.copy(alpha = 0.5f)),
    body1 = LightTypography.body1.copy(color = Color.White.copy(alpha = 0.5f)),
    body2 = LightTypography.body2.copy(color = Color.White.copy(alpha = 0.5f)),
    caption = LightTypography.caption.copy(color = Color.White.copy(alpha = 0.8f)),
)

val PinkTypography = LightTypography.copy(
    body1 = LightTypography.body1.copy(color = Color.Black.copy(alpha = 0.5f)),
)