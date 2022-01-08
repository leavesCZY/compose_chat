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
import github.leavesc.compose_chat.ui.weigets.CircleCoilImage
import github.leavesc.compose_chat.ui.weigets.CoilImage

/**
 * @Author: leavesC
 * @Date: 2021/10/26 11:00
 * @Desc:
 * @Github：https://github.com/leavesC
 */
private val messageDetailMock = MessageDetail(
    msgId = "业志陈",
    timestamp = 1635218361,
    state = MessageState.Completed,
    sender = PersonProfile.Empty,
    isSelfMessage = true
)

private val textMessageMock = TextMessage(
    detail = messageDetailMock,
    msg = "公众号：字节数组，希望对你有所帮助"
)

private val imageMessageMock = ImageMessage(
    detail = messageDetailMock,
    imagePath = ""
)

private val timeMessageMock = TimeMessage(targetMessage = textMessageMock)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFriendTextMessageItem() {
    FriendMessageContainer(
        message = textMessageMock,
        showPartyName = true,
        messageContent = {
            TextMessage(message = textMessageMock)
        },
        onClickAvatar = {

        },
        onClickMessage = {

        },
        onLongClickMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSelfTextMessageItem() {
    SelfMessageContainer(
        message = textMessageMock,
        messageContent = {
            TextMessage(message = textMessageMock)
        },
        onClickAvatar = {

        },
        onClickMessage = {

        },
        onLongClickMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFriendImageMessageItem() {
    FriendMessageContainer(
        message = imageMessageMock,
        showPartyName = true,
        messageContent = {
            ImageMessage(message = imageMessageMock)
        },
        onClickAvatar = {

        },
        onClickMessage = {

        },
        onLongClickMessage = {

        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTimeMessageItem() {
    TimeMessage(timeMessage = timeMessageMock)
}

@Composable
fun MessageItems(
    message: Message,
    showPartyName: Boolean,
    onClickAvatar: (Message) -> Unit,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit,
) {
    val isSelfMessage = message.messageDetail.isSelfMessage
    val unit = when (message) {
        is TextMessage -> {
            if (isSelfMessage) {
                SelfMessageContainer(
                    message = message,
                    messageContent = {
                        TextMessage(message = message)
                    },
                    onClickAvatar = onClickAvatar,
                    onClickMessage = onClickMessage,
                    onLongClickMessage = onLongClickMessage
                )
            } else {
                FriendMessageContainer(
                    message = message,
                    showPartyName = showPartyName,
                    messageContent = {
                        TextMessage(message = message)
                    },
                    onClickAvatar = onClickAvatar,
                    onClickMessage = onClickMessage,
                    onLongClickMessage = onLongClickMessage
                )
            }
        }
        is TimeMessage -> {
            TimeMessage(timeMessage = message)
        }
        is ImageMessage -> {
            if (isSelfMessage) {
                SelfMessageContainer(
                    message = message,
                    messageContent = {
                        ImageMessage(message = message)
                    },
                    onClickAvatar = onClickAvatar,
                    onClickMessage = onClickMessage,
                    onLongClickMessage = onLongClickMessage
                )
            } else {
                FriendMessageContainer(
                    message = message,
                    showPartyName = showPartyName,
                    messageContent = {
                        ImageMessage(message = message)
                    },
                    onClickAvatar = onClickAvatar,
                    onClickMessage = onClickMessage,
                    onLongClickMessage = onLongClickMessage
                )
            }
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
private val messageShape = RoundedCornerShape(size = 6.dp)
private val timeMessageShape = RoundedCornerShape(size = 4.dp)
private val imageSize = 120.dp

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
private fun SelfMessageContainer(
    message: Message,
    messageContent: @Composable (Message) -> Unit,
    onClickAvatar: (Message) -> Unit,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
    ) {
        val (avatarRefs, showNameRefs, messageRefs, messageStateRefs) = createRefs()
        CircleCoilImage(
            data = message.messageDetail.sender.faceUrl,
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                }
                .size(size = avatarSize)
                .clickable(onClick = {
                    onClickAvatar(message)
                })
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRefs) {
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
        Box(
            modifier = Modifier
                .constrainAs(ref = messageRefs) {
                    top.linkTo(
                        anchor = showNameRefs.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    end.linkTo(anchor = showNameRefs.end)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .combinedClickable(
                    onClick = {
                        onClickMessage(message)
                    },
                    onLongClick = {
                        onLongClickMessage(message)
                    }
                )
        ) {
            messageContent(message)
        }
        StateMessage(
            modifier = Modifier.constrainAs(ref = messageStateRefs) {
                top.linkTo(anchor = messageRefs.top)
                bottom.linkTo(anchor = messageRefs.bottom)
                end.linkTo(anchor = messageRefs.start, margin = textMessageHorizontalPadding)
            },
            messageState = message.messageDetail.state,
        )
    }
}

@Composable
private fun FriendMessageContainer(
    message: Message,
    showPartyName: Boolean,
    messageContent: @Composable (Message) -> Unit,
    onClickAvatar: (Message) -> Unit,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
    ) {
        val (avatarRefs, showNameRefs, messageRefs, messageStateRefs) = createRefs()
        CircleCoilImage(
            data = message.messageDetail.sender.faceUrl,
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    top.linkTo(anchor = parent.top)
                    start.linkTo(anchor = parent.start)
                }
                .size(size = avatarSize)
                .clickable(onClick = {
                    onClickAvatar(message)
                })
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRefs) {
                    top.linkTo(
                        anchor = avatarRefs.top
                    )
                    start.linkTo(
                        anchor = avatarRefs.end,
                        margin = textMessageHorizontalPadding
                    )
                },
            text = if (showPartyName) {
                message.messageDetail.sender.showName
            } else {
                ""
            },
            style = messageSenderNameStyle(),
            textAlign = TextAlign.Start,
        )
        Box(
            modifier = Modifier
                .constrainAs(ref = messageRefs) {
                    top.linkTo(
                        anchor = showNameRefs.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    start.linkTo(anchor = showNameRefs.start)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .combinedClickable(
                    onClick = {
                        onClickMessage(message)
                    },
                    onLongClick = {
                        onLongClickMessage(message)
                    }
                )
        ) {
            messageContent(message)
        }
        StateMessage(
            modifier = Modifier.constrainAs(ref = messageStateRefs) {
                top.linkTo(anchor = messageRefs.top)
                bottom.linkTo(anchor = messageRefs.bottom)
                start.linkTo(anchor = messageRefs.end, margin = textMessageHorizontalPadding)
            },
            messageState = message.messageDetail.state,
        )
    }
}

@Composable
private fun TextMessage(
    message: TextMessage,
) {
    Text(
        modifier = Modifier
            .clip(shape = messageShape)
            .background(color = textMessageBgColor)
            .padding(
                horizontal = textMessageInnerHorizontalPadding,
                vertical = textMessageInnerVerticalPadding
            ),
        text = message.msg,
        style = textMessageStyle(),
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun ImageMessage(
    message: ImageMessage
) {
    CoilImage(
        modifier = Modifier
            .clip(shape = messageShape)
            .size(size = imageSize),
        data = message.imagePath,
        builder = {
            placeholder(drawable = null)
        })
}

@Composable
private fun TimeMessage(timeMessage: TimeMessage) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .background(color = Color.LightGray.copy(alpha = 0.8f), shape = timeMessageShape)
            .padding(all = 4.dp),
        text = timeMessage.messageDetail.chatTime,
        style = timeMessageStyle()
    )
}

@Composable
private fun StateMessage(modifier: Modifier, messageState: MessageState) {
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
fun LoadMoreMessage() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        CircularProgressIndicator()
    }
}