package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.base.model.MessageState
import github.leavesc.compose_chat.base.model.TimeMessage

/**
 * @Author: leavesC
 * @Date: 2021/7/5 23:34
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun TimeMessageItem(timeMessage: TimeMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = timeMessage.chatTime,
            style = MaterialTheme.typography.subtitle1,
            fontSize = 13.sp,
            modifier = Modifier
                .background(Color.Gray.copy(alpha = 0.2f))
                .padding(all = 4.dp)
        )
    }
}

@Composable
fun MessageStateItem(modifier: Modifier, messageState: MessageState) {
    val unit = when (messageState) {
        MessageState.Completed -> {
            return
        }
        MessageState.SendFailed -> {
            Image(
                modifier = modifier.size(size = 20.dp),
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.Red)
            )
        }
        MessageState.Sending -> {
            CircularProgressIndicator(
                modifier = modifier.size(size = 20.dp),
                color = Color.Red,
                strokeWidth = 2.dp
            )
        }
    }
}