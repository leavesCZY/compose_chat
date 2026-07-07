package github.leavesczy.compose_chat.ui.chat.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageViewState
import github.leavesczy.compose_chat.widgets.ComponentImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun MessagePanel(pageViewState: ChatPageViewState) {
    val isImeVisible = rememberUpdatedState(newValue = WindowInsets.isImeVisible)
    LaunchedEffect(key1 = pageViewState.listState) {
        launch {
            pageViewState.scrollToLatestMessageFlow
                .collectLatest {
                    delay(timeMillis = 10L)
                    pageViewState.listState.animateScrollToItem(index = 0)
                }
        }
        launch {
            snapshotFlow {
                isImeVisible.value
            }.filter {
                it
            }.collectLatest {
                pageViewState.listState.animateScrollToItem(index = 0)
            }
        }
    }
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
            key = { message ->
                message.detail.msgId
            },
            contentType = { message ->
                when (message) {
                    is TimeMessage -> {
                        "TimeMessage"
                    }

                    is SystemMessage -> {
                        "SystemMessage"
                    }

                    is TextMessage -> {
                        if (message.detail.isOwnMessage) {
                            "ownTextMessage"
                        } else {
                            "friendTextMessage"
                        }
                    }

                    is ImageMessage -> {
                        if (message.detail.isOwnMessage) {
                            "ownImageMessage"
                        } else {
                            "friendImageMessage"
                        }
                    }
                }
            }
        ) { message ->
            ChatMessageItem(
                message = message,
                pageViewState = pageViewState
            )
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: Message,
    pageViewState: ChatPageViewState
) {
    when (message) {
        is TimeMessage -> {
            TimeMessage(
                modifier = Modifier,
                message = message
            )
        }

        is SystemMessage -> {
            SystemMessage(
                modifier = Modifier,
                message = message
            )
        }

        is TextMessage, is ImageMessage -> {
            if (message.detail.isOwnMessage) {
                OwnMessageContainer(
                    modifier = Modifier,
                    message = message,
                    onClickAvatar = pageViewState.onClickAvatar,
                    messageContent = {
                        MessageBubbleContent(
                            message = message,
                            onClickMessage = pageViewState.onClickMessage
                        )
                    }
                )
            } else {
                FriendMessageContainer(
                    modifier = Modifier,
                    message = message,
                    onClickAvatar = pageViewState.onClickAvatar,
                    showPartName = pageViewState.chat is Chat.Group,
                    messageContent = {
                        MessageBubbleContent(
                            message = message,
                            onClickMessage = pageViewState.onClickMessage
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MessageBubbleContent(
    message: Message,
    onClickMessage: (Message) -> Unit
) {
    when (message) {
        is TextMessage -> {
            TextMessage(
                modifier = Modifier,
                message = message
            )
        }

        is ImageMessage -> {
            ImageMessage(
                modifier = Modifier,
                message = message,
                onClickMessage = onClickMessage
            )
        }

        is TimeMessage, is SystemMessage -> Unit
    }
}

@Composable
private fun OwnMessageContainer(
    modifier: Modifier,
    message: Message,
    onClickAvatar: (message: Message) -> Unit,
    messageContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
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
    modifier: Modifier,
    message: Message,
    showPartName: Boolean,
    onClickAvatar: (message: Message) -> Unit,
    messageContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
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
                    messageState = message.detail.state
                )
            }
        }
    }
}

@Composable
private fun Avatar(
    modifier: Modifier,
    message: Message,
    onClickAvatar: (message: Message) -> Unit
) {
    ComponentImage(
        modifier = modifier
            .size(size = 44.dp)
            .clip(shape = RoundedCornerShape(size = 6.dp))
            .clickable(
                onClick = {
                    onClickAvatar(message)
                }
            ),
        model = message.detail.sender.avatarUrl
    )
}

@Composable
private fun Nickname(
    modifier: Modifier,
    nickname: String
) {
    Text(
        modifier = modifier,
        text = nickname,
        fontSize = 13.sp,
        lineHeight = 14.sp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
    )
}

@Composable
private fun TextMessage(
    modifier: Modifier,
    message: TextMessage
) {
    SelectionContainer(
        modifier = modifier
    ) {
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
                            bottomStart = baseRadius
                        )
                    } else {
                        RoundedCornerShape(
                            topStart = specialRadius,
                            topEnd = baseRadius,
                            bottomEnd = baseRadius,
                            bottomStart = baseRadius
                        )
                    }
                )
                .wrapContentWidth(
                    align = if (isOwnMessage) {
                        Alignment.End
                    } else {
                        Alignment.Start
                    }
                )
                .background(
                    color = if (isOwnMessage) {
                        AppTheme.colorScheme.c_FFE2E1EC_FF45464F.color
                    } else {
                        AppTheme.colorScheme.c_FF5386E5_FF5386E5.color
                    }
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            text = message.formatMessage,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            softWrap = true,
            textAlign = TextAlign.Start,
            color = if (isOwnMessage) {
                AppTheme.colorScheme.c_FF3A3D4D_FFFFFFFF.color
            } else {
                AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            }
        )
    }
}

@Composable
private fun ImageMessage(
    modifier: Modifier,
    message: ImageMessage,
    onClickMessage: (Message) -> Unit
) {
    val localDensity = LocalDensity.current
    val previewImage = message.previewImage
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = if (message.detail.isOwnMessage) {
            Alignment.TopEnd
        } else {
            Alignment.TopStart
        }
    ) {
        val imageWidth = previewImage.width
        val imageHeight = previewImage.height
        val imageMinWidthDp = maxWidth / 10f * 4
        val layout = remember(key1 = message.detail.msgId) {
            val isALegalWidthAndHeight = imageWidth > 0 && imageHeight > 0
            if (isALegalWidthAndHeight) {
                val ratio = 1.0f * imageWidth / imageHeight
                val imageWidthDp = with(localDensity) {
                    imageWidth.toDp()
                }
                val imageMaxWidthDp = maxWidth / 10f * 9
                val width = if (imageWidthDp <= imageMinWidthDp) {
                    imageMinWidthDp
                } else if (imageWidthDp < imageMaxWidthDp) {
                    imageWidthDp
                } else {
                    imageMaxWidthDp
                }
                width to ratio
            } else {
                imageMinWidthDp to 1.0f
            }
        }
        val (mWidth, mRatio) = layout
        ComponentImage(
            modifier = Modifier
                .width(width = mWidth)
                .aspectRatio(ratio = mRatio)
                .clip(shape = RoundedCornerShape(size = 6.dp))
                .clickable(onClick = {
                    onClickMessage(message)
                }),
            model = previewImage.url,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}

@Composable
private fun TimeMessage(
    modifier: Modifier,
    message: TimeMessage
) {
    Text(
        modifier = modifier
            .padding(top = 20.dp)
            .background(
                color = AppTheme.colorScheme.c_66CCCCCC_66CCCCCC.color,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 3.dp),
        text = message.formatMessage,
        fontSize = 11.sp,
        lineHeight = 12.sp,
        color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
    )
}

@Composable
private fun SystemMessage(
    modifier: Modifier,
    message: SystemMessage
) {
    Text(
        modifier = modifier
            .background(
                color = AppTheme.colorScheme.c_66CCCCCC_66CCCCCC.color,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        text = message.formatMessage,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
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
                    color = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
                    strokeWidth = 2.dp
                )
            }

            is MessageState.Failed -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    imageVector = Icons.Outlined.Error,
                    colorFilter = ColorFilter.tint(color = AppTheme.colorScheme.c_FFFF545C_FFFA525A.color),
                    contentDescription = null
                )
            }

            MessageState.Success -> {

            }
        }
    }
}