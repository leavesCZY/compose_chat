package github.leavesczy.compose_chat.extend

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color

/**
 * @Author: leavesCZY
 * @Date: 2021/10/26 10:50
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
fun Modifier.scrim(color: Color): Modifier = drawWithContent {
    drawContent()
    drawRect(color = color)
}