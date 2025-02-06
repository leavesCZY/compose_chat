package github.leavesczy.compose_chat.extend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return clickable(
        onClickLabel = null,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}