package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageViewState
import github.leavesczy.compose_chat.ui.widgets.ComponentImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MessagePanel(pageViewState: ChatPageViewState, pageAction: ChatPageAction) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = pageViewState.listState,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 60.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        items(
            items = pageViewState.messageList.value,
            key = {
                it.detail.msgId
            },
            contentType = {
                when (it) {
                    is TimeMessage -> {
                        "TimeMessage"
                    }

                    is SystemMessage -> {
                        "SystemMessage"
                    }

                    is TextMessage -> {
                        val isOwnMessage = it.detail.isOwnMessage
                        if (isOwnMessage) {
                            "ownTextMessage"
                        } else {
                            "fiendTextMessage"
                        }
                    }

                    is ImageMessage -> {
                        val isOwnMessage = it.detail.isOwnMessage
                        if (isOwnMessage) {
                            "ownImageMessage"
                        } else {
                            "fiendImageMessage"
                        }
                    }
                }
            }
        ) { message ->
            when (message) {
                is TimeMessage -> {
                    TimeMessage(message = message)
                }

                is SystemMessage -> {
                    SystemMessage(message = message)
                }

                is TextMessage, is ImageMessage -> {
                    val messageContent = @Composable {
                        when (message) {
                            is TextMessage -> {
                                TextMessage(
                                    message = message,
                                    onClickMessage = pageAction.onClickMessage,
                                    onLongClickMessage = pageAction.onLongClickMessage
                                )
                            }

                            is ImageMessage -> {
                                ImageMessage(
                                    message = message,
                                    onClickMessage = pageAction.onClickMessage
                                )
                            }

                            else -> {
                                throw IllegalArgumentException()
                            }
                        }
                    }
                    if (message.detail.isOwnMessage) {
                        OwnMessageContainer(
                            message = message,
                            messageContent = messageContent
                        )
                    } else {
                        FriendMessageContainer(
                            message = message,
                            onClickAvatar = pageAction.onClickAvatar,
                            showPartDetail = pageViewState.chat is Chat.GroupChat,
                            messageContent = messageContent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnMessageContainer(
    message: Message,
    messageContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.Top)
                .weight(weight = 1f, fill = false)
                .padding(end = 6.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StateMessage(messageState = message.detail.state)
            messageContent()
        }
        ComponentImage(
            modifier = Modifier
                .size(size = 42.dp)
                .clip(shape = CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    shape = CircleShape
                ),
            model = message.detail.sender.faceUrl
        )
    }
}

@Composable
private fun FriendMessageContainer(
    message: Message,
    showPartDetail: Boolean,
    onClickAvatar: (Message) -> Unit,
    messageContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 0.dp, top = 10.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        ComponentImage(
            modifier = Modifier
                .padding(end = 6.dp)
                .size(size = 42.dp)
                .clip(shape = CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    shape = CircleShape
                )
                .clickable(
                    onClick = {
                        onClickAvatar(message)
                    }
                ),
            model = message.detail.sender.faceUrl
        )
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            if (showPartDetail) {
                Text(
                    modifier = Modifier.padding(end = 30.dp),
                    text = message.detail.sender.showName,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .fillMaxWidth(fraction = 0.90f)
                    .wrapContentWidth(align = Alignment.Start),
                contentAlignment = Alignment.TopStart
            ) {
                messageContent()
            }
        }
    }
}

@Composable
private fun TextMessage(
    message: TextMessage,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit
) {
    Text(
        modifier = Modifier
            .clip(
                shape = if (message.detail.isOwnMessage) {
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 6.dp,
                        bottomEnd = 20.dp,
                        bottomStart = 20.dp
                    )
                } else {
                    RoundedCornerShape(
                        topStart = 6.dp,
                        topEnd = 20.dp,
                        bottomEnd = 20.dp,
                        bottomStart = 20.dp
                    )
                }
            )
            .background(
                color = if (message.detail.isOwnMessage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.inverseSurface
                }
            )
            .combinedClickable(
                onClick = {
                    onClickMessage(message)
                },
                onLongClick = {
                    onLongClickMessage(message)
                }
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        text = message.formatMessage,
        fontSize = 16.sp,
        color = if (message.detail.isOwnMessage) {
            Color.White
        } else {
            MaterialTheme.colorScheme.inverseOnSurface
        },
        textAlign = TextAlign.Start
    )
}

@Composable
private fun ImageMessage(
    message: ImageMessage,
    onClickMessage: (Message) -> Unit
) {
    ComponentImage(
        modifier = Modifier
            .size(
                width = message.widgetWidthDp.dp,
                height = message.widgetHeightDp.dp
            )
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .clickable {
                onClickMessage(message)
            },
        model = message.previewImage.url
    )
}

@Composable
private fun TimeMessage(message: TimeMessage) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .background(
                color = Color.LightGray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
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
            .background(
                color = Color.LightGray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(all = 4.dp),
        text = message.formatMessage,
        fontSize = 12.sp
    )
}

@Composable
private fun RowScope.StateMessage(messageState: MessageState) {
    Box(
        modifier = Modifier
            .align(alignment = Alignment.CenterVertically)
            .padding(end = 8.dp)
            .size(size = 20.dp)
    ) {
        when (messageState) {
            MessageState.Sending -> {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }

            is MessageState.SendFailed -> {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Warning,
                    colorFilter = ColorFilter.tint(color = Color.Red),
                    contentDescription = null
                )
            }

            MessageState.Completed -> {

            }
        }
    }
}