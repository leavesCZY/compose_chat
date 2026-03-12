package github.leavesczy.compose_chat.ui.chat.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.ComponentImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MessagePanel(pageViewState: ChatPageViewState, chatPageAction: ChatPageAction) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = pageViewState.listState,
        reverseLayout = true,
        contentPadding = PaddingValues(top = 10.dp, bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 30.dp)
    ) {
        items(
            items = pageViewState.messageList,
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
                        if (it.detail.isOwnMessage) {
                            "ownTextMessage"
                        } else {
                            "fiendTextMessage"
                        }
                    }

                    is ImageMessage -> {
                        if (it.detail.isOwnMessage) {
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
                                TextMessage(message = message)
                            }

                            is ImageMessage -> {
                                ImageMessage(
                                    message = message,
                                    onClickMessage = chatPageAction.onClickMessage
                                )
                            }

                            is TimeMessage, is SystemMessage -> {
                                throw IllegalArgumentException()
                            }
                        }
                    }
                    if (message.detail.isOwnMessage) {
                        OwnMessageContainer(
                            message = message,
                            onClickAvatar = chatPageAction.onClickAvatar,
                            messageContent = messageContent
                        )
                    } else {
                        FriendMessageContainer(
                            message = message,
                            onClickAvatar = chatPageAction.onClickAvatar,
                            showPartName = pageViewState.chat is Chat.Group,
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
            .fillMaxWidth()
            .padding(end = 10.dp),
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
                modifier = Modifier
                    .padding(start = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.End
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MessageState(
                    modifier = Modifier,
                    messageState = message.detail.state
                )
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
            .fillMaxWidth()
            .padding(start = 10.dp),
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
                modifier = Modifier
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
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
                MessageState(
                    modifier = Modifier,
                    messageState = MessageState.Completed
                )
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
                color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
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
        lineHeight = 14.sp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
    )
}

@Composable
private fun TextMessage(message: TextMessage) {
    SelectionContainer {
        val isOwnMessage = message.detail.isOwnMessage
        val baseRadius = 14.dp
        val specialRadius = 2.dp
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
                        ComposeChatTheme.colorScheme.c_FFE2E1EC_FF45464F.color
                    } else {
                        ComposeChatTheme.colorScheme.c_FF5386E5_FF5386E5.color
                    }
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            text = message.formatMessage,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Start,
            softWrap = true,
            color = if (isOwnMessage) {
                ComposeChatTheme.colorScheme.c_FF3A3D4D_FFFFFFFF.color
            } else {
                ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            }
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ImageMessage(
    message: ImageMessage,
    onClickMessage: (Message) -> Unit
) {
    val localDensity = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier,
        contentAlignment = if (message.detail.isOwnMessage) {
            Alignment.TopEnd
        } else {
            Alignment.TopStart
        }
    ) {
        val ratio: Float
        val imageWidgetWidth: Dp
        val imageWidth = message.previewImage.width
        val imageHeight = message.previewImage.height
        val imageWidgetMinWidthDp = maxWidth / 10f * 4
        val isALegalWidthAndHeight = imageWidth > 0 && imageHeight > 0
        if (isALegalWidthAndHeight) {
            ratio = 1.0f * imageWidth / imageHeight
            val imageWidthDp = with(localDensity) {
                imageWidth.toDp()
            }
            val imageWidgetMaxWidthDp = maxWidth / 10f * 9
            imageWidgetWidth = if (imageWidthDp <= imageWidgetMinWidthDp) {
                imageWidgetMinWidthDp
            } else if (imageWidthDp < imageWidgetMaxWidthDp) {
                imageWidthDp
            } else {
                imageWidgetMaxWidthDp
            }
        } else {
            ratio = 1.0f
            imageWidgetWidth = imageWidgetMinWidthDp
        }
        ComponentImage(
            modifier = Modifier
                .width(width = imageWidgetWidth)
                .aspectRatio(ratio = ratio)
                .clip(shape = RoundedCornerShape(size = 12.dp))
                .clickable {
                    onClickMessage(message)
                },
            model = message.previewImage.url,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )
    }
}

@Composable
private fun TimeMessage(message: TimeMessage) {
    Text(
        modifier = Modifier
            .padding(top = 20.dp)
            .background(
                color = ComposeChatTheme.colorScheme.c_66CCCCCC_66CCCCCC.color,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        text = message.formatMessage,
        fontSize = 11.sp,
        lineHeight = 12.sp,
        color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
    )
}

@Composable
private fun SystemMessage(message: SystemMessage) {
    Text(
        modifier = Modifier
            .background(
                color = ComposeChatTheme.colorScheme.c_66CCCCCC_66CCCCCC.color,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        text = message.formatMessage,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
    )
}

@Composable
private fun MessageState(
    modifier: Modifier,
    messageState: MessageState
) {
    Box(
        modifier = modifier
            .size(size = 20.dp)
    ) {
        when (messageState) {
            MessageState.Sending -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
                    strokeWidth = 2.dp
                )
            }

            is MessageState.SendFailed -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    imageVector = Icons.Outlined.Error,
                    colorFilter = ColorFilter.tint(color = ComposeChatTheme.colorScheme.c_FFFF545C_FFFA525A.color),
                    contentDescription = null
                )
            }

            MessageState.Completed -> {

            }
        }
    }
}