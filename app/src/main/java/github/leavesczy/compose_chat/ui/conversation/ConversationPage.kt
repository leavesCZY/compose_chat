package github.leavesczy.compose_chat.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationPageViewState
import github.leavesczy.compose_chat.ui.widgets.ComponentImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ConversationPage(pageViewState: ConversationPageViewState) {
    val conversationList by pageViewState.conversationList
    if (conversationList.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.45f)
                .wrapContentSize(align = Alignment.BottomCenter),
            text = "Empty",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = pageViewState.listState.value,
            contentPadding = PaddingValues(bottom = 30.dp),
        ) {
            items(
                items = conversationList,
                key = {
                    it.id
                },
                contentType = {
                    "Conversation"
                }
            ) {
                ConversationItem(
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
    conversation: Conversation,
    onClickConversation: (Conversation) -> Unit,
    deleteConversation: (Conversation) -> Unit,
    pinConversation: (Conversation, Boolean) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    Box(
        modifier = Modifier
            .then(
                other = if (conversation.isPinned) {
                    Modifier.scrim(color = Color(0x26CCCCCC))
                } else {
                    Modifier
                }
            )
            .fillMaxWidth()
            .height(height = 70.dp)
            .combinedClickable(
                onClick = {
                    onClickConversation(conversation)
                },
                onLongClick = {
                    menuExpanded = true
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                ComponentImage(
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .padding(horizontal = 12.dp)
                        .size(size = 50.dp)
                        .clip(shape = RoundedCornerShape(size = 6.dp)),
                    model = conversation.faceUrl
                )
                val unreadMessageCount = conversation.unreadMessageCount
                if (unreadMessageCount > 0) {
                    val count = if (unreadMessageCount > 99) {
                        "99+"
                    } else {
                        unreadMessageCount.toString()
                    }
                    Text(
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .padding(top = 2.dp)
                            .size(size = 20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .wrapContentSize(align = Alignment.Center),
                        text = count,
                        color = Color.White,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight()
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f),
                        text = conversation.name,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier,
                        text = conversation.lastMessage.detail.conversationTime,
                        fontSize = 12.sp
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(top = 2.dp),
                    text = conversation.formatMsg,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f)
                )
                HorizontalDivider(
                    modifier = Modifier,
                    thickness = 0.2.dp
                )
            }
        }
        MoreActionDropdownMenu(
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
private fun MoreActionDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    conversation: Conversation,
    deleteConversation: (Conversation) -> Unit,
    pinConversation: (Conversation, Boolean) -> Unit

) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        DropdownMenu(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                modifier = Modifier,
                text = {
                    Text(
                        modifier = Modifier,
                        text = if (conversation.isPinned) {
                            "取消置顶"
                        } else {
                            "置顶会话"
                        },
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                onClick = {
                    onDismissRequest()
                    pinConversation(conversation, !conversation.isPinned)
                }
            )
            DropdownMenuItem(
                modifier = Modifier,
                text = {
                    Text(
                        modifier = Modifier,
                        text = "删除会话",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                onClick = {
                    onDismissRequest()
                    deleteConversation(conversation)
                }
            )
        }
    }
}