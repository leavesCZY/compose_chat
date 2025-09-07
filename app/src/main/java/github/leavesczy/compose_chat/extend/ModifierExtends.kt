package github.leavesczy.compose_chat.extend

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
fun Modifier.scrim(color: Color): Modifier {
    return drawWithContent {
        drawContent()
        drawRect(color = color)
    }
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return clickable(
        onClickLabel = null,
        indication = null,
        interactionSource = null,
        onClick = onClick
    )
}