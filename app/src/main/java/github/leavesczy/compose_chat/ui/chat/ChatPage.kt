package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToPreviewImagePage
import github.leavesczy.compose_chat.extend.viewModelInstance
import github.leavesczy.compose_chat.logic.ChatViewModel
import github.leavesczy.compose_chat.model.Page
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.flow.filter

/**
 * @Author: leavesCZY
 * @Date: 2021/7/3 23:53
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ChatPage(listState: LazyListState, chat: Chat) {
    val chatViewModel = viewModelInstance {
        ChatViewModel(chat = chat)
    }

    val topBarTitle by chatViewModel.topBarTitle.collectAsState()
    val chatPageState by chatViewModel.chatPageState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val navHostController = LocalNavHostController.current

    val onClickAvatar: (Message) -> Unit = remember {
        {
            val messageSenderId = it.messageDetail.sender.id
            if (messageSenderId.isNotBlank()) {
                navHostController.navigate(
                    route = Page.FriendProfilePage.generateRoute(friendId = messageSenderId)
                )
            }
        }
    }
    val onClickMessage: (Message) -> Unit = remember {
        {
            when (it) {
                is ImageMessage -> {
                    val imagePath = it.previewUrl
                    if (imagePath.isBlank()) {
                        showToast("图片路径为空")
                    } else {
                        navHostController.navToPreviewImagePage(imagePath = imagePath)
                    }
                }
                else -> {

                }
            }
        }
    }
    val onLongClickMessage: (Message) -> Unit = remember {
        {
            when (it) {
                is TextMessage -> {
                    val msg = it.formatMessage
                    if (msg.isNotEmpty()) {
                        clipboardManager.setText(AnnotatedString(msg))
                        showToast("已复制")
                    }
                }
                else -> {

                }
            }
        }
    }

    LaunchedEffect(key1 = chatPageState.mushScrollToBottom) {
        snapshotFlow {
            chatPageState.mushScrollToBottom
        }.filter {
            it
        }.collect {
            listState.scrollToItem(index = 0, scrollOffset = 0)
        }
    }

    LaunchedEffect(key1 = listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex
        }.filter {
            !chatPageState.loadFinish
        }.collect {
            chatViewModel.loadMoreMessage()
        }
    }

    Scaffold(
        topBar = {
            ChatPageTopBar(
                title = topBarTitle,
                onClickBackMenu = {
                    navHostController.popBackStack()
                },
                onClickMoreMenu = {
                    when (chat) {
                        is PrivateChat -> {
                            navHostController.navigate(
                                route = Page.FriendProfilePage.generateRoute(friendId = chat.id)
                            )
                        }
                        is GroupChat -> {
                            navHostController.navigate(
                                route = Page.GroupProfilePage.generateRoute(groupId = chat.id)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatPageBottomBar(
                sendText = {
                    chatViewModel.sendTextMessage(text = it.text)
                }, sendImage = {
                    chatViewModel.sendImageMessage(mediaResources = it)
                })
        }
    ) { contentPadding ->
        MessagePanel(
            listState = listState,
            chat = chat,
            contentPadding = contentPadding,
            chatPageState = chatPageState,
            onClickAvatar = onClickAvatar,
            onClickMessage = onClickMessage,
            onLongClickMessage = onLongClickMessage
        )
    }
}