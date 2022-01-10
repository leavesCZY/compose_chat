package github.leavesc.compose_chat.ui.theme

import androidx.compose.ui.graphics.Color

val PrimaryColorLight = Color(0xFFF04E4E)
val PrimaryVariantColorLight = Color(0xFFFFFFFF)
val SecondaryColorLight = Color(0xFFF4F4F4)
val SecondaryVariantColorLight = Color(0xFFF4F4F4)
val BackgroundColorLight = Color(0xFFFFFFFF)
val SurfaceColorLight = Color(0xFFF04E4E)

val PrimaryColorDark = Color(0xFFE65956)
val PrimaryVariantColorDark = Color(0xFF22202A)
val SecondaryColorDark = Color(0xFF3A3D4D)
val SecondaryVariantColorDark = Color(0xFF3A3D4D)
val BackgroundColorDark = Color(0xFF22202C)
val SurfaceColorDark = Color(0xFFE65956)

val PrimaryColorBlue = Color(0xFF03A9F4)
val PrimaryVariantColorBlue = Color(0xFF03A9F4)
val SecondaryColorBlue = Color(0xFFF4F4F4)
val SecondaryVariantColorBlue = Color(0xFFF4F4F4)
val BackgroundColorBlue = Color(0xFFFFFFFF)
val SurfaceColorBlue = Color(0xFFFFFFFF)

private val textMessageBgColorLight = PrimaryColorLight
private val textMessageBgColorDark = SecondaryColorDark
private val textMessageBgColorBlue = PrimaryColorBlue

val textMessageBgColor: Color
    get() {
        return when (AppThemeCache.currentTheme) {
            AppTheme.Light -> textMessageBgColorLight
            AppTheme.Dark -> textMessageBgColorDark
            AppTheme.Blue -> textMessageBgColorBlue
            AppTheme.Gray -> textMessageBgColorLight
        }
    }