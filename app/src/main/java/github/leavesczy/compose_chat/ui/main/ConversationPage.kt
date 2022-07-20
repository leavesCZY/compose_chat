package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeast
import github.leavesczy.compose_chat.common.model.Conversation
import github.leavesczy.compose_chat.model.ConversationPageViewState
import github.leavesczy.compose_chat.model.MainPageAction
import github.leavesczy.compose_chat.ui.widgets.CoilImage
import github.leavesczy.compose_chat.ui.widgets.CommonDivider
import github.leavesczy.compose_chat.ui.widgets.EmptyView

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:55
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ConversationPage(
    conversationPageViewState: ConversationPageViewState,
    mainPageAction: MainPageAction
) {
    Surface {
        val conversationList = conversationPageViewState.conversationList
        if (conversationList.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(
                state = conversationPageViewState.listState,
                contentPadding = PaddingValues(bottom = 60.dp),
            ) {
                items(items = conversationList, key = {
                    it.id
                }, contentType = {
                    "Conversation"
                }, itemContent = {
                    ConversationItem(
                        conversation = it,
                        onClickConversation = mainPageAction.onClickConversation,
                        onDeleteConversation = mainPageAction.onDeleteConversation,
                        onPinnedConversation = mainPageAction.onPinnedConversation
                    )
                })
            }
        }
    }
}

@Composable
private fun LazyItemScope.ConversationItem(
    conversation: Conversation,
    onClickConversation: (Conversation) -> Unit,
    onDeleteConversation: (Conversation) -> Unit,
    onPinnedConversation: (Conversation, Boolean) -> Unit
) {
    val avatarSize = 55.dp
    val padding = 8.dp
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    val bgColor = if (conversation.isPinned) {
        Color.LightGray.copy(alpha = 0.15f)
    } else {
        Color.Transparent
    }
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClickConversation(conversation)
                },
                onLongClick = {
                    menuExpanded = true
                }
            )
            .background(color = bgColor),
    ) {
        val (avatarRef, unreadMessageCountRef, nicknameRef, lastMsgRef, timeRef, dividerRef, dropdownMenuRef) = createRefs()
        val verticalChain =
            createVerticalChain(nicknameRef, lastMsgRef, chainStyle = ChainStyle.Packed)
        constrain(ref = verticalChain) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        CoilImage(
            modifier = Modifier
                .padding(horizontal = padding * 1.5f, vertical = padding)
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
            data = conversation.faceUrl
        )
        if (conversation.unreadMessageCount > 0) {
            val count =
                if (conversation.unreadMessageCount > 99) "99+" else conversation.unreadMessageCount.toString()
            Text(
                modifier = Modifier
                    .constrainAs(ref = unreadMessageCountRef) {
                        top.linkTo(anchor = avatarRef.top, margin = padding / 2)
                        end.linkTo(anchor = avatarRef.end, margin = padding)
                        width = Dimension.preferredWrapContent.atLeast(dp = 22.dp)
                        height = Dimension.preferredWrapContent.atLeast(dp = 22.dp)
                    }
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .wrapContentSize(align = Alignment.Center),
                text = count,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier
                .constrainAs(ref = nicknameRef) {
                    linkTo(
                        start = avatarRef.end,
                        end = timeRef.start,
                        endMargin = padding
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 2.dp),
            text = conversation.name,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = lastMsgRef) {
                    linkTo(start = avatarRef.end, end = parent.end, endMargin = padding)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 2.dp),
            text = conversation.formatMsg,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = timeRef) {
                    centerVerticallyTo(other = nicknameRef)
                    end.linkTo(anchor = parent.end, margin = padding)
                    height = Dimension.wrapContent
                },
            text = conversation.lastMessage.messageDetail.conversationTime,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = dividerRef) {
                    linkTo(start = avatarRef.end, end = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                },
        )
        Box(
            modifier = Modifier
                .constrainAs(ref = dropdownMenuRef) {
                    linkTo(start = parent.start, end = parent.end, bias = 0.3f)
                    linkTo(top = parent.top, bottom = parent.bottom)
                }
        ) {
            DropdownMenu(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {
                DropdownMenuItem(text = {
                    Text(
                        text = if (conversation.isPinned) "取消置顶" else "置顶会话",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }, onClick = {
                    menuExpanded = false
                    onPinnedConversation(conversation, !conversation.isPinned)
                })
                DropdownMenuItem(text = {
                    Text(text = "删除会话", style = MaterialTheme.typography.bodyMedium)
                }, onClick = {
                    menuExpanded = false
                    onDeleteConversation(conversation)
                })
            }
        }
    }
}