package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atMost
import github.leavesczy.compose_chat.base.model.*
import github.leavesczy.compose_chat.ui.weigets.CircleImage
import github.leavesczy.compose_chat.ui.weigets.CoilImage

/**
 * @Author: leavesCZY
 * @Date: 2021/10/26 11:00
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MessageItems(
    message: Message,
    showPartyName: Boolean,
    onClickAvatar: (Message) -> Unit,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit,
) {
    if (message is TimeMessage) {
        TimeMessage(message = message)
        return
    }
    val messageContent = @Composable {
        when (message) {
            is TextMessage -> {
                TextMessage(message = message)
            }
            is ImageMessage -> {
                ImageMessage(message = message)
            }
            else -> {

            }
        }
    }
    val isSelfMessage = message.messageDetail.isSelfMessage
    if (isSelfMessage) {
        SelfMessageContainer(
            message = message,
            messageContent = {
                messageContent()
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
                messageContent()
            },
            onClickAvatar = onClickAvatar,
            onClickMessage = onClickMessage,
            onLongClickMessage = onLongClickMessage
        )
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
private val imageSize = 120.dp
private val messageShape = RoundedCornerShape(size = 6.dp)
private val timeMessageShape = RoundedCornerShape(size = 4.dp)
private val messageBackground: Color
    @Composable
    get() {
        return MaterialTheme.colorScheme.primary
    }

private val messageSenderNameStyle: TextStyle
    @Composable
    get() {
        return MaterialTheme.typography.bodySmall
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
        CircleImage(
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
            style = messageSenderNameStyle,
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
                .clip(shape = messageShape)
                .background(color = messageBackground)
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
        CircleImage(
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
            style = messageSenderNameStyle,
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
                .clip(shape = messageShape)
                .background(color = messageBackground)
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
            .padding(
                horizontal = textMessageInnerHorizontalPadding,
                vertical = textMessageInnerVerticalPadding
            ),
        text = message.msg,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun TimeMessage(message: TimeMessage) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .background(color = Color.LightGray.copy(alpha = 0.4f), shape = timeMessageShape)
            .padding(all = 3.dp),
        text = message.messageDetail.chatTime,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
    )
}

@Composable
private fun ImageMessage(
    message: ImageMessage
) {
    CoilImage(
        modifier = Modifier.size(size = imageSize),
        data = message.imagePath
    )
}

@Composable
private fun StateMessage(modifier: Modifier, messageState: MessageState) {
    val unit = when (messageState) {
        MessageState.Completed -> {

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
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
fun LoadMoreMessage() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}