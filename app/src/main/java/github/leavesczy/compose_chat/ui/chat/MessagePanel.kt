package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atMost
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.model.ChatPageAction
import github.leavesczy.compose_chat.model.ChatPageViewState
import github.leavesczy.compose_chat.ui.widgets.CoilImage

/**
 * @Author: leavesCZY
 * @Date: 2021/10/26 11:00
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MessagePanel(
    contentPadding: PaddingValues,
    chatPageViewState: ChatPageViewState,
    chatPageAction: ChatPageAction
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = contentPadding),
        state = chatPageViewState.listState,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        for (message in chatPageViewState.messageList) {
            item(key = message.messageDetail.msgId) {
                MessageItems(
                    message = message,
                    showPartyName = chatPageViewState.chat is GroupChat,
                    chatPageAction = chatPageAction
                )
            }
        }
        if (chatPageViewState.showLoadMore) {
            item(key = "loadMore") {
                MessageLoading()
            }
        }
    }
}

@Composable
private fun MessageItems(
    message: Message,
    showPartyName: Boolean,
    chatPageAction: ChatPageAction,
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
                throw IllegalArgumentException()
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
            chatPageAction = chatPageAction
        )
    } else {
        FriendMessageContainer(
            message = message,
            showPartyName = showPartyName,
            messageContent = {
                messageContent()
            },
            chatPageAction = chatPageAction
        )
    }
}

private val avatarSize = 48.dp
private val itemHorizontalPadding = 10.dp
private val itemVerticalPadding = 10.dp
private val textMessageWidthAtMost = 230.dp
private val textMessageSenderNameVerticalPadding = 3.dp
private val textMessageHorizontalPadding = 8.dp
private val messageShape = RoundedCornerShape(size = 6.dp)
private val timeMessageShape = RoundedCornerShape(size = 4.dp)

@Composable
private fun SelfMessageContainer(
    message: Message,
    messageContent: @Composable (Message) -> Unit,
    chatPageAction: ChatPageAction,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
    ) {
        val (avatarRef, showNameRef, messageRef, messageStateRef) = createRefs()
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                }
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .clickable(onClick = {
                    chatPageAction.onClickAvatar(message)
                }),
            data = message.messageDetail.sender.faceUrl
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRef) {
                    top.linkTo(
                        anchor = avatarRef.top
                    )
                    end.linkTo(
                        anchor = avatarRef.start,
                        margin = textMessageHorizontalPadding
                    )
                },
            text = "",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
        Box(
            modifier = Modifier
                .constrainAs(ref = messageRef) {
                    top.linkTo(
                        anchor = showNameRef.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    end.linkTo(anchor = showNameRef.end)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .clip(shape = messageShape)
                .combinedClickable(
                    onClick = {
                        chatPageAction.onClickMessage(message)
                    },
                    onLongClick = {
                        chatPageAction.onLongClickMessage(message)
                    }
                )
        ) {
            messageContent(message)
        }
        StateMessage(
            modifier = Modifier.constrainAs(ref = messageStateRef) {
                top.linkTo(anchor = messageRef.top)
                bottom.linkTo(anchor = messageRef.bottom)
                end.linkTo(anchor = messageRef.start, margin = textMessageHorizontalPadding)
            },
            messageState = message.messageDetail.state
        )
    }
}

@Composable
private fun FriendMessageContainer(
    message: Message,
    showPartyName: Boolean,
    messageContent: @Composable (Message) -> Unit,
    chatPageAction: ChatPageAction
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding
            )
    ) {
        val (avatarRef, showNameRef, messageRef, messageStateRef) = createRefs()
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    top.linkTo(anchor = parent.top)
                    start.linkTo(anchor = parent.start)
                }
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .clickable(onClick = {
                    chatPageAction.onClickAvatar(message)
                }),
            data = message.messageDetail.sender.faceUrl
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRef) {
                    top.linkTo(
                        anchor = avatarRef.top
                    )
                    start.linkTo(
                        anchor = avatarRef.end,
                        margin = textMessageHorizontalPadding
                    )
                },
            text = if (showPartyName) {
                message.messageDetail.sender.showName
            } else {
                ""
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
        )
        Box(
            modifier = Modifier
                .constrainAs(ref = messageRef) {
                    top.linkTo(
                        anchor = showNameRef.bottom,
                        margin = textMessageSenderNameVerticalPadding
                    )
                    start.linkTo(anchor = showNameRef.start)
                    width = Dimension.preferredWrapContent.atMost(dp = textMessageWidthAtMost)
                }
                .clip(shape = messageShape)
                .combinedClickable(
                    onClick = {
                        chatPageAction.onClickMessage(message)
                    },
                    onLongClick = {
                        chatPageAction.onLongClickMessage(message)
                    }
                )
        ) {
            messageContent(message)
        }
        StateMessage(
            modifier = Modifier.constrainAs(ref = messageStateRef) {
                top.linkTo(anchor = messageRef.top)
                bottom.linkTo(anchor = messageRef.bottom)
                start.linkTo(anchor = messageRef.end, margin = textMessageHorizontalPadding)
            },
            messageState = message.messageDetail.state,
        )
    }
}

@Composable
private fun TextMessage(message: TextMessage) {
    Text(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(
                horizontal = 6.dp,
                vertical = 6.dp
            ),
        text = message.formatMessage,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
        textAlign = TextAlign.Start
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
        text = message.formatMessage,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
    )
}

@Composable
private fun ImageMessage(message: ImageMessage) {
    val preview = message.preview
    val imageWidth = preview.width
    val imageHeight = preview.height
    val maxImageRatio = 1.9f
    val widgetWidth = 190.dp
    val widgetHeight = if (imageWidth <= 0 || imageHeight <= 0) {
        widgetWidth
    } else {
        widgetWidth * (minOf(maxImageRatio, 1.0f * imageHeight / imageWidth))
    }
    CoilImage(
        modifier = Modifier.size(width = widgetWidth, height = widgetHeight),
        data = preview.url
    )
}

@Composable
private fun StateMessage(modifier: Modifier, messageState: MessageState) {
    when (messageState) {
        MessageState.Sending -> {
            CircularProgressIndicator(
                modifier = modifier.size(size = 20.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }
        is MessageState.SendFailed -> {
            Image(
                modifier = modifier.size(size = 20.dp),
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.Red)
            )
        }
        MessageState.Completed -> {

        }
    }
}

@Composable
private fun MessageLoading() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}