package github.leavesczy.compose_chat.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeast
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.base.model.Conversation
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationPageViewState
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.widgets.CoilImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ConversationPage() {
    val conversationViewModel = viewModel<ConversationViewModel>()
    ConversationContentPage(pageViewState = conversationViewModel.pageViewState)
}

@Composable
private fun ConversationContentPage(pageViewState: ConversationPageViewState) {
    val conversationList = pageViewState.conversationList
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
            state = pageViewState.listState,
            contentPadding = PaddingValues(bottom = 60.dp),
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
                    pageViewState = pageViewState
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.ConversationItem(
    conversation: Conversation,
    pageViewState: ConversationPageViewState
) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .background(color = MaterialTheme.colorScheme.background)
            .then(
                other = if (conversation.isPinned) {
                    Modifier.scrim(color = Color(0x26CCCCCC))
                } else {
                    Modifier
                }
            )
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    pageViewState.onClickConversation(conversation)
                },
                onLongClick = {
                    menuExpanded = true
                }
            )
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
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                }
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                .size(size = 50.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            data = conversation.faceUrl
        )
        if (conversation.unreadMessageCount > 0) {
            val count = if (conversation.unreadMessageCount > 99) {
                "99+"
            } else {
                conversation.unreadMessageCount.toString()
            }
            Text(
                modifier = Modifier
                    .constrainAs(ref = unreadMessageCountRef) {
                        start.linkTo(anchor = avatarRef.end)
                        top.linkTo(anchor = avatarRef.top, margin = 2.dp)
                        end.linkTo(anchor = avatarRef.end)
                        width = Dimension.preferredWrapContent.atLeast(dp = 20.dp)
                        height = Dimension.preferredWrapContent.atLeast(dp = 20.dp)
                    }
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .wrapContentSize(align = Alignment.Center),
                text = count,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier
                .constrainAs(ref = nicknameRef) {
                    linkTo(
                        start = avatarRef.end,
                        end = timeRef.start,
                        startMargin = 12.dp,
                        endMargin = 12.dp
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 1.dp),
            text = conversation.name,
            fontSize = 17.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = lastMsgRef) {
                    linkTo(start = nicknameRef.start, end = parent.end, endMargin = 12.dp)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 1.dp),
            text = conversation.formatMsg,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = timeRef) {
                    centerVerticallyTo(other = nicknameRef)
                    end.linkTo(anchor = parent.end, margin = 12.dp)
                    height = Dimension.wrapContent
                },
            text = conversation.lastMessage.messageDetail.conversationTime,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Divider(
            modifier = Modifier
                .constrainAs(ref = dividerRef) {
                    linkTo(start = avatarRef.end, end = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                },
            thickness = 0.2.dp
        )
        MoreActionDropdownMenu(
            modifier = Modifier
                .constrainAs(ref = dropdownMenuRef) {
                    linkTo(start = parent.start, end = parent.end, bias = 0.3f)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            },
            conversation = conversation,
            deleteConversation = pageViewState.deleteConversation,
            pinConversation = pageViewState.pinConversation
        )
    }
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
    Box(
        modifier = modifier
    ) {
        DropdownMenu(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                modifier = Modifier,
                text = {
                    Text(
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