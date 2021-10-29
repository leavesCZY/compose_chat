package github.leavesc.compose_chat.ui.theme

import androidx.compose.ui.graphics.Color
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.model.AppTheme

val BackgroundColorLight = Color(0xFFFFFFFF)
val PrimaryColorLight = Color(0xFFE65956)
val PrimaryVariantColorLight = Color(0xFFFFFFFF)
val SurfaceColorLight = Color(0xFFE65956)
val SecondaryColorLight = Color(0xFFF4F4F4)

val BackgroundColorDark = Color(0xFF333333)
val PrimaryColorDark = Color(0xFFE65956)
val PrimaryVariantColorDark = Color(0xFF333333)
val SurfaceColorDark = Color(0xFFE65956)
val SecondaryColorDark = Color(0xFF1B1717)

val BackgroundColorBlue = Color(0xFFFFFFFF)
val PrimaryColorBlue = Color(0xFF03A9F4)
val PrimaryVariantColorBlue = Color(0xFF03A9F4)
val SurfaceColorBlue = Color(0xFFFFFFFF)
val SecondaryColorBlue = Color(0xFFF4F4F4)

val BackgroundColorPink = Color(0xFFFFFFFF)
val PrimaryColorPink = Color(0xFFF3447F)
val PrimaryVariantColorPink = Color(0xFFF3447F)
val SurfaceColorPink = Color(0xFFFFFFFF)
val SecondaryColorPink = Color(0xFFF4F4F4)

val selfMsgBgColorLight = Color(0xFFE65956)
val friendMsgBgColorLight = Color(0xFFE65956)

val selfMsgBgColorDark = Color(0xFF1B1717)
val friendMsgBgColorDark = Color(0xFF1B1717)

val selfMsgBgColorBlue = Color(0xFF03A9F4)
val friendMsgBgColorBlue = Color(0xFF03A9F4)

val selfMsgBgColorPink = Color(0xFFF3447F)
val friendMsgBgColorPink = Color(0xFFF3447F)

val selfMsgBgColor: Color
    get() {
        return when (AppThemeCache.currentTheme) {
            AppTheme.Light -> selfMsgBgColorLight
            AppTheme.Dark -> selfMsgBgColorDark
            AppTheme.Blue -> selfMsgBgColorBlue
            AppTheme.Pink -> selfMsgBgColorPink
        }
    }

val friendMsgBgColor: Color
    get() {
        return when (AppThemeCache.currentTheme) {
            AppTheme.Light -> friendMsgBgColorLight
            AppTheme.Dark -> friendMsgBgColorDark
            AppTheme.Blue -> friendMsgBgColorBlue
            AppTheme.Pink -> friendMsgBgColorPink
        }
    }

val textMessageColor = Color.White.copy(alpha = 0.9f)