package github.leavesc.compose_chat.ui.theme

import androidx.compose.ui.graphics.Color
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.model.AppTheme

val BackgroundColorLight = Color(0xFFFFFFFF)
val PrimaryColorLight = Color(0xFFFF5722)
val PrimaryVariantColorLight = Color(0xFFFFFFFF)
val SurfaceColorLight = Color(0xFFE65956)
val SecondaryColorLight = Color(0xFFF4F4F4)

val BackgroundColorDark = Color(0xFF333333)
val PrimaryColorDark = Color(0xCCEC6A41)
val PrimaryVariantColorDark = Color(0xFF333333)
val SurfaceColorDark = Color(0xCCF16663)
val SecondaryColorDark = Color(0xFF1B1717)

val BackgroundColorBlue = Color(0xFFFFFFFF)
val PrimaryColorBlue = Color(0xFF2196F3)
val PrimaryVariantColorBlue = Color(0xFF03A9F4)
val SurfaceColorBlue = Color(0xFFFFFFFF)
val SecondaryColorBlue = Color(0xFFF4F4F4)

val BackgroundColorPink = Color(0xFFFFFFFF)
val PrimaryColorPink = Color(0xFFF15D8F)
val PrimaryVariantColorPink = Color(0xFFF3447F)
val SurfaceColorPink = Color(0xFFFFFFFF)
val SecondaryColorPink = Color(0xFFF4F4F4)

val selfMsgBgColorLight = Color(0x800CA4E9)
val friendMsgBgColorLight = Color(0x8024F0DD)

val selfMsgBgColorDark = Color(0x8047F7E2)
val friendMsgBgColorDark = Color(0x80EB734D)

val selfMsgBgColorBlue = Color(0x801F93F1)
val friendMsgBgColorBlue = Color(0x80467B96)

val selfMsgBgColorPink = Color(0xFFFBC02D)
val friendMsgBgColorPink = Color(0x80F3447F)

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