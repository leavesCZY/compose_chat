package github.leavesczy.compose_chat.extensions

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color

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

fun Modifier.clickableNoRippleNotCheck(onClick: () -> Unit): Modifier {
    return clickable(
        onClickLabel = "notCheck",
        indication = null,
        interactionSource = null,
        onClick = onClick
    )
}