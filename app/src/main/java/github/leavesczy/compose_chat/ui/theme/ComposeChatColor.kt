package github.leavesczy.compose_chat.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * @Author: CZY
 * @Date: 2025/9/7 13:37
 * @Desc:
 */
class ComposeChatColor(
    day: Color,
    night: Color,
    darkTheme: Boolean
) {

    val color = if (darkTheme) {
        night
    } else {
        day
    }

}

data class ComposeChatColorScheme(
    val darkTheme: Boolean,
    val c_FF42A5F5_FF26A69A: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFF42A5F5),
        night = Color(color = 0xFF26A69A),
        darkTheme = darkTheme
    ),
    val c_FF001018_DEFFFFFF: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFF001018),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    ),
    val c_FFFFFFFF_FF101010: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF101010),
        darkTheme = darkTheme
    ),
    val c_FFFFFFFF_FFFFFFFF: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    ),
    val c_FF384F60_99FFFFFF: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFF384F60),
        night = Color(color = 0x99FFFFFF),
        darkTheme = darkTheme
    ),
    val c_FFFFFFFF_FF161616: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF161616),
        darkTheme = darkTheme
    ),
    val c_FF5775A8_FF45464F: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0XFF5775A8),
        night = Color(color = 0xFF45464F),
        darkTheme = darkTheme
    ),
    val c_FFE2E1EC_FF45464F: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFE2E1EC),
        night = Color(color = 0xFF45464F),
        darkTheme = darkTheme
    ),
    val c_FF3A3D4D_FFFFFFFF: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFF3A3D4D),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    ),
    val c_803A3D4D_B3FFFFFF: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0x803A3D4D),
        night = Color(color = 0xB3FFFFFF),
        darkTheme = darkTheme
    ),
    val c_FFFFFFFF_FF22202A: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF22202A),
        darkTheme = darkTheme
    ),
    val c_80000000_99000000: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0x80000000),
        night = Color(color = 0x99000000),
        darkTheme = darkTheme
    ),
    val c_FFEFF1F3_FF333333: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFEFF1F3),
        night = Color(color = 0xFF333333),
        darkTheme = darkTheme
    ),
    val c_FF1C1B1F_FFFFFFFF: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFF1C1B1F),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    ),
    val c_FF22202A_FF22202A: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFF22202A),
        night = Color(color = 0xFF22202A),
        darkTheme = darkTheme
    ),
    val c_FFFF545C_FFFA525A: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFFF545C),
        night = Color(color = 0xFFFA525A),
        darkTheme = darkTheme
    ),
    val c_66CCCCCC_66CCCCCC: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0x66CCCCCC),
        night = Color(color = 0x66CCCCCC),
        darkTheme = darkTheme
    ),
    val c_33CCCCCC_33CCCCCC: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0x33CCCCCC),
        night = Color(color = 0x33CCCCCC),
        darkTheme = darkTheme
    ),
    val c_FFEFF1F3_FF22202A: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0xFFEFF1F3),
        night = Color(color = 0xFF22202A),
        darkTheme = darkTheme
    ),
    val c_33000000_33000000: ComposeChatColor = ComposeChatColor(
        day = Color(color = 0x33000000),
        night = Color(color = 0x33000000),
        darkTheme = darkTheme
    ),
)

val LocalComposeChatColorScheme = staticCompositionLocalOf<ComposeChatColorScheme> {
    error("CompositionLocal LocalComposeChatColorScheme not present")
}

val LightComposeChatColorScheme = ComposeChatColorScheme(darkTheme = false)

val DarkComposeChatColorScheme = ComposeChatColorScheme(darkTheme = true)