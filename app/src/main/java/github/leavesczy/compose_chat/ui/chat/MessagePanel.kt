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
import androidx.compose.foundation.layout.aspectRatio
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
        contentPadding = PaddingValues(
            start = 10.dp,
            end = 10.dp,
            top = 10.dp,
            bottom = 60.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 30.dp, alignment = Alignment.Top),
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
                            onClickAvatar = pageAction.onClickAvatar,
                            messageContent = messageContent
                        )
                    } else {
                        FriendMessageContainer(
                            message = message,
                            onClickAvatar = pageAction.onClickAvatar,
                            showPartName = pageViewState.chat is Chat.GroupChat,
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
    onClickAvatar: (Message) -> Unit,
    messageContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(space = 3.dp)
        ) {
            Nickname(
                modifier = Modifier
                    .padding(start = 6.dp),
                nickname = ""
            )
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(
                    space = 20.dp,
                    alignment = Alignment.End
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MessageState(messageState = message.detail.state)
                messageContent()
            }
        }
        Avatar(
            modifier = Modifier
                .padding(start = 6.dp),
            message = message,
            onClickAvatar = onClickAvatar
        )
    }
}

@Composable
private fun FriendMessageContainer(
    message: Message,
    showPartName: Boolean,
    onClickAvatar: (Message) -> Unit,
    messageContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Avatar(
            modifier = Modifier
                .padding(end = 6.dp),
            message = message,
            onClickAvatar = onClickAvatar
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(space = 3.dp)
        ) {
            Nickname(
                modifier = Modifier
                    .padding(end = 6.dp),
                nickname = if (showPartName) {
                    message.detail.sender.showName
                } else {
                    ""
                }
            )
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(
                    space = 20.dp,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(weight = 1f, fill = false),
                    contentAlignment = Alignment.TopStart
                ) {
                    messageContent()
                }
                MessageState(messageState = MessageState.Completed)
            }
        }
    }
}

@Composable
private fun Avatar(
    modifier: Modifier = Modifier,
    message: Message,
    onClickAvatar: (Message) -> Unit,
) {
    ComponentImage(
        modifier = modifier
            .size(size = 46.dp)
            .clip(shape = CircleShape)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clickable(
                onClick = {
                    onClickAvatar(message)
                }
            ),
        model = message.detail.sender.faceUrl
    )
}

@Composable
private fun Nickname(
    modifier: Modifier = Modifier,
    nickname: String,
) {
    Text(
        modifier = modifier,
        text = nickname,
        fontSize = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun TextMessage(
    message: TextMessage,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit
) {
    val isOwnMessage = message.detail.isOwnMessage
    val baseRadius = 14.dp
    val specialRadius = 4.dp
    Text(
        modifier = Modifier
            .clip(
                shape = if (isOwnMessage) {
                    RoundedCornerShape(
                        topStart = baseRadius,
                        topEnd = specialRadius,
                        bottomEnd = baseRadius,
                        bottomStart = baseRadius,
                    )
                } else {
                    RoundedCornerShape(
                        topStart = specialRadius,
                        topEnd = baseRadius,
                        bottomEnd = baseRadius,
                        bottomStart = baseRadius,
                    )
                }
            )
            .wrapContentWidth(
                align = if (isOwnMessage) {
                    Alignment.End
                } else {
                    Alignment.Start
                },
            )
            .background(
                color = if (isOwnMessage) {
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
            .padding(horizontal = 8.dp, vertical = 6.dp),
        text = message.formatMessage,
        color = if (isOwnMessage) {
            Color.White
        } else {
            MaterialTheme.colorScheme.inverseOnSurface
        },
        fontSize = 16.sp,
        lineHeight = 24.sp,
        softWrap = true,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun ImageMessage(
    message: ImageMessage,
    onClickMessage: (Message) -> Unit
) {
    val isOwnMessage = message.detail.isOwnMessage
    val imageWidth = message.previewImage.width
    val imageHeight = message.previewImage.height
    val ratio = if (imageWidth <= 0 || imageHeight <= 0) {
        1f
    } else {
        1.0f * imageWidth / imageHeight
    }
    ComponentImage(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.75f)
            .wrapContentWidth(
                align = if (isOwnMessage) {
                    Alignment.End
                } else {
                    Alignment.Start
                }
            )
            .aspectRatio(ratio = ratio)
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
            .padding(top = 20.dp)
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
            .padding(top = 20.dp)
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
private fun MessageState(messageState: MessageState) {
    Box(
        modifier = Modifier
            .size(size = 20.dp)
    ) {
        when (messageState) {
            MessageState.Sending -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }

            is MessageState.SendFailed -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
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