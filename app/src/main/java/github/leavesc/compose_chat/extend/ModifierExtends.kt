package github.leavesc.compose_chat.extend

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * @Author: leavesC
 * @Date: 2021/10/26 10:50
 * @Desc:
 * @Github：https://github.com/leavesC
 */
fun Modifier.scrim(colors: List<Color>): Modifier = drawWithContent {
    drawContent()
    drawRect(Brush.verticalGradient(colors))
}