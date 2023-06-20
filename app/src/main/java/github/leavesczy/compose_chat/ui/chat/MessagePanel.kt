package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ImageMessage
import github.leavesczy.compose_chat.base.model.Message
import github.leavesczy.compose_chat.base.model.MessageState
import github.leavesczy.compose_chat.base.model.SystemMessage
import github.leavesczy.compose_chat.base.model.TextMessage
import github.leavesczy.compose_chat.base.model.TimeMessage
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageViewState
import github.leavesczy.compose_chat.ui.widgets.CoilImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MessagePanel(chatPageViewState: ChatPageViewState, chatPageAction: ChatPageAction) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = chatPageViewState.listState,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        for (message in chatPageViewState.messageList) {
            item(key = message.messageDetail.msgId) {
                MessageItems(
                    message = message,
                    showPartyName = chatPageViewState.chat is Chat.GroupChat,
                    chatPageAction = chatPageAction
                )
            }
        }
    }
}

@Composable
private fun MessageItems(message: Message, showPartyName: Boolean, chatPageAction: ChatPageAction) {
    if (message is TimeMessage) {
        TimeMessage(message = message)
        return
    }
    if (message is SystemMessage) {
        SystemMessage(message = message)
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
    if (message.messageDetail.isSelfMessage) {
        SelfMessageContainer(
            message = message,
            chatPageAction = chatPageAction,
            messageContent = messageContent
        )
    } else {
        FriendMessageContainer(
            message = message,
            showPartyName = showPartyName,
            chatPageAction = chatPageAction,
            messageContent = messageContent
        )
    }
}

private val avatarSize = 44.dp
private val itemHorizontalPadding = 14.dp
private val itemVerticalPadding = 10.dp
private val textMessageWidthAtMost = 230.dp
private val textMessageSenderNameVerticalPadding = 3.dp
private val textMessageHorizontalPadding = 8.dp
private val messageShape = RoundedCornerShape(size = 6.dp)
private val timeMessageShape = RoundedCornerShape(size = 4.dp)

@Composable
private fun SelfMessageContainer(
    message: Message,
    chatPageAction: ChatPageAction,
    messageContent: @Composable () -> Unit
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
                .clip(shape = RoundedCornerShape(size = 6.dp))
                .clickable(onClick = {
                    chatPageAction.onClickAvatar(message)
                }),
            data = message.messageDetail.sender.faceUrl
        )
        Text(
            modifier = Modifier.constrainAs(ref = showNameRef) {
                top.linkTo(anchor = avatarRef.top)
                end.linkTo(anchor = avatarRef.start, margin = textMessageHorizontalPadding)
            },
            text = "",
            fontSize = 16.sp,
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
                    }, onLongClick = {
                        chatPageAction.onLongClickMessage(message)
                    }
                )
        ) {
            messageContent()
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
    chatPageAction: ChatPageAction,
    messageContent: @Composable () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = itemHorizontalPadding, vertical = itemVerticalPadding)
    ) {
        val (avatarRef, showNameRef, messageRef, messageStateRef) = createRefs()
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    top.linkTo(anchor = parent.top)
                    start.linkTo(anchor = parent.start)
                }
                .size(size = avatarSize)
                .clip(shape = RoundedCornerShape(size = 6.dp))
                .clickable(
                    onClick = {
                        chatPageAction.onClickAvatar(message)
                    }
                ),
            data = message.messageDetail.sender.faceUrl
        )
        Text(
            modifier = Modifier.constrainAs(ref = showNameRef) {
                top.linkTo(anchor = avatarRef.top)
                start.linkTo(anchor = avatarRef.end, margin = textMessageHorizontalPadding)
            },
            text = if (showPartyName) {
                message.messageDetail.sender.showName
            } else {
                ""
            },
            fontSize = 12.sp,
            textAlign = TextAlign.Start
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
            messageContent()
        }
        StateMessage(
            modifier = Modifier
                .constrainAs(ref = messageStateRef) {
                    top.linkTo(anchor = messageRef.top)
                    bottom.linkTo(anchor = messageRef.bottom)
                    start.linkTo(
                        anchor = messageRef.end, margin = textMessageHorizontalPadding
                    )
                },
            messageState = message.messageDetail.state
        )
    }
}

@Composable
private fun TextMessage(message: TextMessage) {
    Text(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        text = message.formatMessage,
        fontSize = 16.sp,
        color = Color.White,
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
        fontSize = 11.sp
    )
}

@Composable
private fun SystemMessage(message: SystemMessage) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .background(color = Color.LightGray.copy(alpha = 0.4f), shape = timeMessageShape)
            .padding(all = 3.dp),
        text = message.formatMessage,
        fontSize = 12.sp
    )
}

@Composable
private fun ImageMessage(message: ImageMessage) {
    CoilImage(
        modifier = Modifier.size(
            width = message.widgetWidthDp.dp,
            height = message.widgetHeightDp.dp
        ),
        data = message.preview.url
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