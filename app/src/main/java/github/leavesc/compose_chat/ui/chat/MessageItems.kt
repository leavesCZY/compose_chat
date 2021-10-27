package github.leavesc.compose_chat.ui.chat

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.ui.theme.friendMsgBgColor
import github.leavesc.compose_chat.ui.theme.selfMsgBgColor
import github.leavesc.compose_chat.ui.weigets.CoilImage

/**
 * @Author: leavesC
 * @Date: 2021/10/26 11:00
 * @Desc:
 * @Github：https://github.com/leavesC
 */
private val friendTextMessageMock = TextMessage.FriendTextMessage(
    msgId = "业志陈",
    timestamp = 1635218361,
    sender = PersonProfile.Empty,
    msg = "公众号：字节数组，希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23"
)

private val selfTextMessageMock = TextMessage.SelfTextMessage(
    msgId = "业志陈",
    state = MessageState.Completed,
    timestamp = 1635218361,
    sender = PersonProfile.Empty,
    msg = "掘金：业志陈，希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23"
)

private val timeMessageMock = TimeMessage(targetMessage = friendTextMessageMock)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewFriendTextMessageItem() {
    FriendTextMessageItem(
        textMessage = friendTextMessageMock,
        onClickFriendAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewSelfTextMessageItem() {
    SelfTextMessageItem(
        textMessage = selfTextMessageMock,
        onClickSelfAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewTimeMessageItem() {
    TimeMessageItem(timeMessage = timeMessageMock)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewMessageStateItem() {
    MessageStateItem(modifier = Modifier, messageState = MessageState.Sending)
}

private val avatarSize = 40.dp
private val itemPaddingTop = 6.dp
private val itemPaddingBottom = 6.dp
private val itemPaddingStart = 14.dp
private val messageStartPadding = 10.dp
private val messageTopPadding = avatarSize * 0.3f
private val messageEndPadding = 10.dp
private val messageInnerPadding = 6.dp
private val messageShape = RoundedCornerShape(size = 6.dp)
private const val messageWidthWeight = 3.5f
private const val messageStateWidthWeight = 1f

@Composable
fun SelfTextMessageItem(
    textMessage: TextMessage,
    onClickSelfAvatar: (Message) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = itemPaddingStart, top = itemPaddingTop, bottom = itemPaddingBottom),
        horizontalArrangement = Arrangement.End,
    ) {
        Box(
            modifier = Modifier
                .weight(weight = messageStateWidthWeight, fill = true)
                .padding(
                    top = messageTopPadding,
                )
                .align(alignment = Alignment.CenterVertically),
            contentAlignment = Alignment.CenterEnd
        ) {
            MessageStateItem(
                modifier = Modifier,
                messageState = textMessage.state,
            )
        }
        Text(
            modifier = Modifier
                .weight(weight = messageWidthWeight, fill = false)
                .padding(
                    start = messageStartPadding,
                    top = messageTopPadding,
                    end = messageEndPadding
                )
                .clip(shape = messageShape)
                .background(color = selfMsgBgColor)
                .pointerInput(key1 = Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onLongPressMessage(textMessage)
                        },
                    )
                }
                .padding(
                    start = messageInnerPadding,
                    top = messageInnerPadding,
                    end = messageInnerPadding,
                    bottom = messageInnerPadding
                ),
            text = textMessage.msg,
            style = MaterialTheme.typography.body1,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Left,
        )
        CoilImage(
            data = textMessage.sender.faceUrl,
            modifier = Modifier
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .clickable(onClick = {
                    onClickSelfAvatar(textMessage)
                })
        )
    }
}

@Composable
fun FriendTextMessageItem(
    textMessage: TextMessage,
    onClickFriendAvatar: (Message) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = itemPaddingStart, top = itemPaddingTop, bottom = itemPaddingBottom)
    ) {
        CoilImage(
            data = textMessage.sender.faceUrl,
            modifier = Modifier
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .clickable {
                    onClickFriendAvatar(textMessage)
                }
        )
        Text(
            modifier = Modifier
                .weight(weight = messageWidthWeight, fill = false)
                .padding(
                    start = messageStartPadding,
                    top = messageTopPadding,
                    end = messageEndPadding
                )
                .clip(shape = messageShape)
                .background(color = friendMsgBgColor)
                .pointerInput(key1 = Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onLongPressMessage(textMessage)
                        },
                    )
                }
                .padding(
                    start = messageInnerPadding,
                    top = messageInnerPadding,
                    end = messageInnerPadding,
                    bottom = messageInnerPadding
                ),
            text = textMessage.msg,
            style = MaterialTheme.typography.body1,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Left,
        )
        Box(
            modifier = Modifier
                .weight(weight = messageStateWidthWeight, fill = true)
                .padding(
                    top = messageTopPadding,
                )
                .align(alignment = Alignment.CenterVertically)
        ) {
            MessageStateItem(
                modifier = Modifier,
                messageState = textMessage.state,
            )
        }
    }
}

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
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = 3.dp))
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

@Composable
fun LoadMoreMessageItem() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        CircularProgressIndicator()
    }
}