package github.leavesc.compose_chat.ui.chat

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.leavesc.compose_chat.base.model.Message
import github.leavesc.compose_chat.base.model.MessageState
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.base.model.TextMessage
import github.leavesc.compose_chat.ui.common.NetworkImage
import github.leavesc.compose_chat.ui.theme.friendMsgBgColor
import github.leavesc.compose_chat.ui.theme.selfMsgBgColor

/**
 * @Author: leavesC
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesC
 */
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewFriendTextMessageItem() {
    FriendTextMessageItem(
        textMessage = TextMessage.FriendTextMessage(
            msgId = "业志陈",
            timestamp = 100,
            sender = PersonProfile.Empty,
            msg = "公众号：字节数组，希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23"
        ),
        onClickFriendAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewSelfTextMessageItem() {
    SelfTextMessageItem(
        textMessage = TextMessage.SelfTextMessage(
            msgId = "业志陈",
            state = MessageState.Completed,
            timestamp = 100,
            sender = PersonProfile.Empty,
            msg = "掘金：业志陈，希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23"
        ),
        onClickSelfAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Composable
fun FriendTextMessageItem(
    textMessage: TextMessage,
    onClickFriendAvatar: (String) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = itemPaddingStart, top = itemPaddingTop, bottom = itemPaddingBottom)
    ) {
        NetworkImage(
            data = textMessage.sender.faceUrl,
            modifier = Modifier
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .clickable {
                    onClickFriendAvatar(textMessage.sender.userId)
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
            style = MaterialTheme.typography.subtitle1,
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
fun SelfTextMessageItem(
    textMessage: TextMessage,
    onClickSelfAvatar: () -> Unit,
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
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Left,
        )
        NetworkImage(
            data = textMessage.sender.faceUrl,
            modifier = Modifier
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .clickable(onClick = onClickSelfAvatar)
        )
    }
}