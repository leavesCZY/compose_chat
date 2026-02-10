package github.leavesczy.compose_chat.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationPageViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.ComponentImage
import github.leavesczy.compose_chat.ui.widgets.ComposeDropdownMenuItem

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ConversationPage(pageViewState: ConversationPageViewState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = pageViewState.listState,
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        serverConnectState(
            modifier = Modifier,
            serverConnectState = pageViewState.serverConnectState
        )
        val conversationList = pageViewState.conversationList
        if (conversationList.isEmpty()) {
            item(
                key = "EmptyPage",
                contentType = "EmptyPage"
            ) {
                EmptyPage(modifier = Modifier)
            }
        } else {
            items(
                items = conversationList,
                key = {
                    it.id
                },
                contentType = {
                    "ConversationItem"
                }
            ) {
                ConversationItem(
                    modifier = Modifier
                        .animateItem(),
                    conversation = it,
                    onClickConversation = pageViewState.onClickConversation,
                    deleteConversation = pageViewState.deleteConversation,
                    pinConversation = pageViewState.pinConversation
                )
            }
        }
    }
}

@Composable
private fun ConversationItem(
    modifier: Modifier,
    conversation: Conversation,
    onClickConversation: (Conversation) -> Unit,
    deleteConversation: (Conversation) -> Unit,
    pinConversation: (Conversation, Boolean) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClickConversation(conversation)
                },
                onLongClick = {
                    menuExpanded = true
                }
            )
            .then(
                other = if (conversation.isPinned) {
                    Modifier
                        .scrim(color = ComposeChatTheme.colorScheme.c_33CCCCCC_33CCCCCC.color)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                modifier = Modifier,
                faceUrl = conversation.faceUrl,
                unreadMessageCount = conversation.unreadMessageCount
            )
            Column(
                modifier = Modifier
                    .weight(weight = 1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(
                    space = 3.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f),
                        text = conversation.name,
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
                    )
                    Text(
                        modifier = Modifier,
                        text = conversation.lastMessage.detail.conversationTime,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = ComposeChatTheme.colorScheme.c_FF384F60_99FFFFFF.color
                    )
                }
                Text(
                    modifier = Modifier,
                    text = conversation.formatMsg,
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ComposeChatTheme.colorScheme.c_FF384F60_99FFFFFF.color
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter),
            thickness = 0.2.dp
        )
        MoreActionDropdownMenu(
            modifier = Modifier
                .align(alignment = Alignment.Center),
            expanded = menuExpanded,
            conversation = conversation,
            onDismissRequest = {
                menuExpanded = false
            },
            deleteConversation = deleteConversation,
            pinConversation = pinConversation
        )
    }
}

@Composable
private fun Avatar(
    modifier: Modifier,
    faceUrl: String,
    unreadMessageCount: Long
) {
    Box(modifier = modifier) {
        ComponentImage(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .padding(end = 12.dp, top = 8.dp, bottom = 8.dp)
                .size(size = 54.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            model = faceUrl
        )
        if (unreadMessageCount > 0) {
            UnreadMessageCount(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(top = 2.dp, end = 2.dp),
                unreadMessageCount = unreadMessageCount
            )
        }
    }
}

@Composable
private fun UnreadMessageCount(
    modifier: Modifier,
    unreadMessageCount: Long
) {
    val count = if (unreadMessageCount > 99L) {
        "99+"
    } else {
        unreadMessageCount.toString()
    }
    Text(
        modifier = modifier
            .size(size = 20.dp)
            .background(
                color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
                shape = CircleShape
            )
            .wrapContentSize(align = Alignment.Center),
        text = count,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
    )
}

@Composable
private fun MoreActionDropdownMenu(
    modifier: Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    conversation: Conversation,
    deleteConversation: (Conversation) -> Unit,
    pinConversation: (Conversation, Boolean) -> Unit
) {
    Box(modifier = modifier) {
        DropdownMenu(
            modifier = Modifier,
            containerColor = ComposeChatTheme.colorScheme.c_FFEFF1F3_FF22202A.color,
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            ComposeDropdownMenuItem(
                modifier = Modifier,
                text = if (conversation.isPinned) {
                    "取消置顶"
                } else {
                    "置顶会话"
                },
                onClick = {
                    onDismissRequest()
                    pinConversation(conversation, !conversation.isPinned)
                }
            )
            ComposeDropdownMenuItem(
                modifier = Modifier,
                text = "删除会话",
                onClick = {
                    onDismissRequest()
                    deleteConversation(conversation)
                }
            )
        }
    }
}

private fun LazyListScope.serverConnectState(
    modifier: Modifier,
    serverConnectState: ServerConnectState
) {
    when (serverConnectState) {
        ServerConnectState.Idle,
        ServerConnectState.Connected -> {
            return
        }

        ServerConnectState.Logout,
        ServerConnectState.Connecting,
        ServerConnectState.ConnectFailed,
        ServerConnectState.UserSigExpired,
        ServerConnectState.KickedOffline -> {
            item(
                key = "serverConnectState",
                contentType = "serverConnectState"
            ) {
                Box(
                    modifier = modifier
                        .animateItem()
                        .fillMaxWidth()
                        .background(color = ComposeChatTheme.colorScheme.c_66CCCCCC_66CCCCCC.color)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        text = "serverConnectState : $serverConnectState ...",
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = ComposeChatTheme.colorScheme.c_FF384F60_99FFFFFF.color
                    )
                }
            }
        }
    }
}

@Composable
internal fun LazyItemScope.EmptyPage(modifier: Modifier) {
    Box(
        modifier = modifier
            .animateItem()
            .fillParentMaxWidth()
            .fillParentMaxHeight(fraction = 0.85f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = "Empty",
            fontSize = 68.sp,
            lineHeight = 70.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
        )
    }
}