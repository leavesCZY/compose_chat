package github.leavesc.compose_chat.ui.chat

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atMost
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.ui.theme.textMessageBgColor
import github.leavesc.compose_chat.ui.weigets.CoilCircleImage
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

private val selfImageMessageMock = ImageMessage(
    msgId = "业志陈",
    state = MessageState.Completed,
    timestamp = 1635218361,
    sender = PersonProfile.Empty,
    imagePath = "掘金：业志陈，希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23"
)


private val timeMessageMock = TimeMessage(targetMessage = friendTextMessageMock)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFriendTextMessageItem() {
    FriendTextMessageItem(
        textMessage = friendTextMessageMock,
        showSenderName = true,
        onClickAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSelfTextMessageItem() {
    SelfTextMessageItem(
        textMessage = selfTextMessageMock,
        onClickAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewImageMessageItem() {
    ImageMessageItem(
        imageMessage = selfImageMessageMock,
        onClickAvatar = {

        },
        onLongPressMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTimeMessageItem() {
    TimeMessageItem(timeMessage = timeMessageMock)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMessageStateItem() {
    MessageStateItem(modifier = Modifier, messageState = MessageState.Sending)
}

@Composable
fun MessageItems(
    message: Message,
    showSenderName: Boolean,
    onClickSelfAvatar: (Message) -> Unit,
    onClickFriendAvatar: (Message) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    val unit = when (message) {
        is TextMessage -> {
            when (message) {
                is TextMessage.SelfTextMessage -> {
                    SelfTextMessageItem(
                        textMessage = message,
                        onClickAvatar = onClickSelfAvatar,
                        onLongPressMessage = onLongPressMessage,
                    )
                }
                is TextMessage.FriendTextMessage -> {
                    FriendTextMessageItem(
                        textMessage = message,
                        showSenderName = showSenderName,
                        onClickAvatar = onClickFriendAvatar,
                        onLongPressMessage = onLongPressMessage,
                    )
                }
            }
        }
        is TimeMessage -> {
            TimeMessageItem(timeMessage = message)
        }
        is ImageMessage -> {
            ImageMessageItem(
                imageMessage = message,
                onClickAvatar = onClickFriendAvatar,
                onLongPressMessage = onLongPressMessage
            )
        }
    }
}

private val avatarSize = 44.dp
private val itemHorizontalPadding = 12.dp
private val itemVerticalPadding = 10.dp

private val textMessageWidthAtMost = 230.dp
private val textMessageSenderNameVerticalPadding = 3.dp
private val textMessageHorizontalPadding = 6.dp
private val textMessageInnerHorizontalPadding = 6.dp
private val textMessageInnerVerticalPadding = 6.dp
private val textMessageShape = RoundedCornerShape(size = 6.dp)
private val timeMessageShape = RoundedCornerShape(size = 4.dp)

private val messageBgColor = Color.Unspecified

@Composable
private fun textMessageStyle(): TextStyle {
    return TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = Color.White,
        letterSpacing = 1.sp,
        lineHeight = 22.sp
    )
}

@Composable
private fun timeMessageStyle(): TextStyle {
    return TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color.White
    )
}

@Composable
private fun messageSenderNameStyle(): TextStyle {
    return MaterialTheme.typography.body1.copy(fontSize = 12.sp)
}

@Composable
private fun SelfTextMessageItem(
    textMessage: TextMessage,
    onClickAvatar: (Message) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
            .background(color = messageBgColor)
    ) {
        val (avatarRefs, showName, message, messageState) = createRefs()
        CoilCircleImage(
            data = textMessage.sender.faceUrl,
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                }
                .size(size = avatarSize)
                .clickable(onClick = {
                    onClickAvatar(textMessage)
                })
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showName) {
                    top.linkTo(
                        anchor = avatarRefs.top
                    )
                    end.linkTo(
                        anchor = avatarRefs.start,
                        margin = textMessageHorizontalPadding
                    )
                },
            text = "",
            style = messageSenderNameStyle(),
            textAlign = TextAlign.End,
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = message) {
                    top.linkTo(
                        anchor = showName.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    end.linkTo(anchor = showName.end)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .clip(shape = textMessageShape)
                .background(color = textMessageBgColor)
                .combinedClickable(
                    onClick = {

                    },
                    onLongClick = {
                        onLongPressMessage(textMessage)
                    }
                )
                .padding(
                    horizontal = textMessageInnerHorizontalPadding,
                    vertical = textMessageInnerVerticalPadding
                ),
            text = textMessage.msg,
            style = textMessageStyle(),
            textAlign = TextAlign.Start,
        )
        MessageStateItem(
            modifier = Modifier.constrainAs(ref = messageState) {
                top.linkTo(anchor = message.top)
                bottom.linkTo(anchor = message.bottom)
                end.linkTo(anchor = message.start, margin = textMessageHorizontalPadding)
            },
            messageState = textMessage.state,
        )
    }
}

@Composable
private fun FriendTextMessageItem(
    textMessage: TextMessage,
    showSenderName: Boolean,
    onClickAvatar: (Message) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
            .background(color = messageBgColor)
    ) {
        val (avatarRefs, showName, message, messageState) = createRefs()
        CoilCircleImage(
            data = textMessage.sender.faceUrl,
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    top.linkTo(anchor = parent.top)
                    start.linkTo(anchor = parent.start)
                }
                .size(size = avatarSize)
                .clickable(onClick = {
                    onClickAvatar(textMessage)
                })
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showName) {
                    top.linkTo(
                        anchor = avatarRefs.top
                    )
                    start.linkTo(
                        anchor = avatarRefs.end,
                        margin = textMessageHorizontalPadding
                    )
                },
            text = if (showSenderName) {
                textMessage.sender.showName
            } else {
                ""
            },
            style = messageSenderNameStyle(),
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = message) {
                    top.linkTo(
                        anchor = showName.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    start.linkTo(anchor = showName.start)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .clip(shape = textMessageShape)
                .background(color = textMessageBgColor)
                .combinedClickable(
                    onClick = {

                    },
                    onLongClick = {
                        onLongPressMessage(textMessage)
                    }
                )
                .padding(
                    horizontal = textMessageInnerHorizontalPadding,
                    vertical = textMessageInnerVerticalPadding
                ),
            text = textMessage.msg,
            style = textMessageStyle(),
            textAlign = TextAlign.Start,
        )
        MessageStateItem(
            modifier = Modifier.constrainAs(ref = messageState) {
                top.linkTo(anchor = message.top)
                bottom.linkTo(anchor = message.bottom)
                start.linkTo(anchor = message.end, margin = textMessageHorizontalPadding)
            },
            messageState = textMessage.state,
        )
    }
}

@Composable
private fun TimeMessageItem(timeMessage: TimeMessage) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .background(color = Color.LightGray.copy(alpha = 0.8f), shape = timeMessageShape)
            .padding(all = 4.dp),
        text = timeMessage.chatTime,
        style = timeMessageStyle()
    )
}

@Composable
private fun MessageStateItem(modifier: Modifier, messageState: MessageState) {
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
private fun ImageMessageItem(
    imageMessage: ImageMessage,
    onClickAvatar: (Message) -> Unit,
    onLongPressMessage: (Message) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
            .background(color = messageBgColor)
    ) {
        val (avatarRefs, showName, message, messageState) = createRefs()
        CoilCircleImage(
            data = imageMessage.sender.faceUrl,
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    top.linkTo(anchor = parent.top)
                    start.linkTo(anchor = parent.start)
                }
                .size(size = avatarSize)
                .clickable(onClick = {
                    onClickAvatar(imageMessage)
                })
        )
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = message) {
                    top.linkTo(
                        anchor = showName.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    start.linkTo(anchor = showName.start)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .clip(shape = textMessageShape)
                .background(color = textMessageBgColor)
                .combinedClickable(
                    onClick = {

                    },
                    onLongClick = {
                        onLongPressMessage(imageMessage)
                    }
                )
                .padding(
                    horizontal = textMessageInnerHorizontalPadding,
                    vertical = textMessageInnerVerticalPadding
                ),
            data = imageMessage.imagePath)
        MessageStateItem(
            modifier = Modifier.constrainAs(ref = messageState) {
                top.linkTo(anchor = message.top)
                bottom.linkTo(anchor = message.bottom)
                start.linkTo(anchor = message.end, margin = textMessageHorizontalPadding)
            },
            messageState = imageMessage.state,
        )
    }
}

@Composable
fun LoadMoreMessageItem() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        CircularProgressIndicator()
    }
}