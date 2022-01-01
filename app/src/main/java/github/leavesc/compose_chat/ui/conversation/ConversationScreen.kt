package github.leavesc.compose_chat.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeast
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.model.ConversationScreenState
import github.leavesc.compose_chat.ui.weigets.CoilCircleImage
import github.leavesc.compose_chat.ui.weigets.CommonDivider
import github.leavesc.compose_chat.ui.weigets.EmptyView

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:55
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ConversationScreen(
    paddingValues: PaddingValues,
    conversationScreenState: ConversationScreenState,
) {
    Scaffold(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
    ) {
        if (conversationScreenState.conversationList.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(state = conversationScreenState.listState) {
                conversationScreenState.conversationList.forEach {
                    item(key = it.id) {
                        ConversationItem(
                            conversation = it,
                            onClickConversation = conversationScreenState.onClickConversation,
                            onDeleteConversation = conversationScreenState.onDeleteConversation,
                            onPinnedConversation = conversationScreenState.onPinnedConversation
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    onClickConversation: (Conversation) -> Unit,
    onDeleteConversation: (Conversation) -> Unit,
    onPinnedConversation: (Conversation, Boolean) -> Unit
) {
    val padding = 10.dp
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    val bgColor = if (conversation.isPinned) {
        Color.LightGray.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colors.background
    }
    ConstraintLayout(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    onClickConversation(conversation)
                },
                onLongClick = {
                    menuExpanded = true
                }
            )
            .fillMaxWidth()
            .background(color = bgColor)
            .padding(top = padding),
    ) {
        val (avatar, unreadMessageCount, nickname, lastMsg, time, divider, dropdownMenu) = createRefs()
        CoilCircleImage(
            data = conversation.faceUrl,
            modifier = Modifier
                .padding(start = padding * 1.5f)
                .size(size = 50.dp)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        if (conversation.unreadMessageCount > 0) {
            val count =
                if (conversation.unreadMessageCount > 99) "99+" else conversation.unreadMessageCount.toString()
            Text(
                text = count,
                color = Color.White,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(ref = unreadMessageCount) {
                        start.linkTo(anchor = avatar.end)
                        end.linkTo(anchor = avatar.end)
                        top.linkTo(anchor = avatar.top, margin = (-6).dp)
                        width = Dimension.preferredWrapContent.atLeast(dp = 22.dp)
                        height = Dimension.preferredWrapContent.atLeast(dp = 22.dp)
                    }
                    .background(color = Color.Red, shape = CircleShape)
                    .wrapContentSize(align = Alignment.Center)
            )
        }
        Text(
            text = conversation.name,
            style = MaterialTheme.typography.subtitle1,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding)
                .constrainAs(ref = nickname) {
                    start.linkTo(anchor = avatar.end)
                    top.linkTo(anchor = avatar.top)
                    end.linkTo(anchor = time.start, margin = padding)
                    width = Dimension.fillToConstraints
                }
        )
        Text(
            text = conversation.lastMessage.messageDetail.conversationTime,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding)
                .constrainAs(ref = time) {
                    centerVerticallyTo(other = nickname)
                    end.linkTo(anchor = parent.end, margin = padding)
                    height = Dimension.wrapContent
                }
        )
        Text(
            text = conversation.formatMsg,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, top = padding / 2)
                .constrainAs(ref = lastMsg) {
                    start.linkTo(anchor = nickname.start)
                    top.linkTo(anchor = nickname.bottom)
                    end.linkTo(anchor = parent.end, margin = padding)
                    width = Dimension.fillToConstraints
                }
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = divider) {
                    start.linkTo(anchor = avatar.end, margin = padding)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = lastMsg.bottom, margin = padding)
                    width = Dimension.fillToConstraints
                },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.TopCenter)
                .constrainAs(ref = dropdownMenu) {
                    start.linkTo(anchor = parent.start)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = parent.top)
                }
        ) {
            DropdownMenu(
                modifier = Modifier.background(color = MaterialTheme.colors.background),
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    onPinnedConversation(conversation, !conversation.isPinned)
                }) {
                    Text(text = if (conversation.isPinned) "取消置顶" else "置顶会话")
                }
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    onDeleteConversation(conversation)
                }) {
                    Text(text = "删除会话")
                }
            }
        }
    }
}