package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.ui.common.CommonDivider
import github.leavesc.compose_chat.ui.common.EmptyView
import github.leavesc.compose_chat.ui.common.CoilImage

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:55
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ConversationScreen(
    listState: LazyListState,
    paddingValues: PaddingValues,
    conversationList: List<Conversation>,
    onClickConversation: (Conversation) -> Unit,
    onDeleteConversation: (Conversation) -> Unit,
    onConversationPinnedChanged: (Conversation, Boolean) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
    ) {
        if (conversationList.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(state = listState) {
                conversationList.forEach {
                    item(key = it.id) {
                        ConversationItem(
                            conversation = it,
                            onClickConversation = onClickConversation,
                            onDeleteConversation = onDeleteConversation,
                            onConversationPinnedChanged = onConversationPinnedChanged
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
fun ConversationItem(
    conversation: Conversation,
    onClickConversation: (Conversation) -> Unit,
    onDeleteConversation: (Conversation) -> Unit,
    onConversationPinnedChanged: (Conversation, Boolean) -> Unit
) {
    val padding = 12.dp
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    ConstraintLayout(
        modifier = Modifier
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onTap = {
                        onClickConversation(conversation)
                    },
                    onLongPress = {
                        menuExpanded = true
                    },
                )
            }
            .fillMaxWidth()
            .let {
                if (conversation.isPinned) {
                    it.background(color = Color.Gray.copy(alpha = 0.1f))
                } else {
                    it.background(color = MaterialTheme.colors.background)
                }
            }
            .padding(top = padding),
    ) {
        val (avatar, unreadMessageCount, nickname, lastMsg, time, divider, dropdownMenu) = createRefs()
        CoilImage(
            data = conversation.faceUrl,
            modifier = Modifier
                .padding(start = padding * 1.5f)
                .size(size = 50.dp)
                .clip(shape = CircleShape)
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
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.sp,
                lineHeight = 0.sp,
                modifier = Modifier
                    .size(24.dp)
                    .padding(all = 2.dp)
                    .background(color = Color.Red, shape = CircleShape)
                    .constrainAs(ref = unreadMessageCount) {
                        start.linkTo(anchor = avatar.end)
                        end.linkTo(anchor = avatar.end)
                        top.linkTo(anchor = avatar.top, margin = (-6).dp)
                    }
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
            text = conversation.lastMessage.conversationTime,
            style = MaterialTheme.typography.subtitle2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, top = padding / 3)
                .constrainAs(ref = time) {
                    top.linkTo(anchor = nickname.top)
                    bottom.linkTo(anchor = nickname.bottom)
                    end.linkTo(anchor = parent.end, margin = padding)
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
                    bottom.linkTo(anchor = parent.bottom)
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
                    onDeleteConversation(conversation)
                }) {
                    Text(text = "删除会话", modifier = Modifier)
                }
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    onConversationPinnedChanged(conversation, !conversation.isPinned)
                }) {
                    Text(text = if (conversation.isPinned) "取消置顶" else "会话置顶", modifier = Modifier)
                }
            }
        }
    }
}