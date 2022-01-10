package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.google.accompanist.insets.LocalWindowInsets
import github.leavesc.compose_chat.base.model.Chat
import github.leavesc.compose_chat.base.model.ImageMessage
import github.leavesc.compose_chat.base.model.Message
import github.leavesc.compose_chat.base.model.TextMessage
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.extend.navToPreviewImageScreen
import github.leavesc.compose_chat.extend.viewModelInstance
import github.leavesc.compose_chat.logic.ChatViewModel
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.chat.bottom_bar.ChatScreenBottomBar
import github.leavesc.compose_chat.ui.chat.top_bar.ChatScreenTopBar
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.flow.filter

/**
 * @Author: leavesC
 * @Date: 2021/7/3 23:53
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ChatScreen(
    listState: LazyListState,
    chat: Chat
) {
    val chatViewModel = viewModelInstance {
        ChatViewModel(chat = chat)
    }

    val screenTopBarTitle by chatViewModel.screenTopBarTitle.collectAsState()
    val chatScreenState by chatViewModel.chatScreenState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val navHostController = LocalNavHostController.current

    val onClickAvatar: (Message) -> Unit = remember {
        {
            val messageSenderId = it.messageDetail.sender.userId
            if (messageSenderId.isNotBlank()) {
                navHostController.navigate(
                    route = Screen.FriendProfileScreen.generateRoute(friendId = messageSenderId)
                )
            }
        }
    }
    val onClickMessage: (Message) -> Unit = remember {
        {
            when (it) {
                is ImageMessage -> {
                    val imagePath = it.imagePath
                    if (imagePath.isBlank()) {
                        showToast("图片路径为空")
                    } else {
                        navHostController.navToPreviewImageScreen(imagePath = imagePath)
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
                    val msg = it.msg
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

    val ime = LocalWindowInsets.current.ime
    LaunchedEffect(key1 = chatScreenState) {
        snapshotFlow {
            chatScreenState.mushScrollToBottom || ime.isVisible
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
            !chatScreenState.loadFinish
        }.collect {
            chatViewModel.loadMoreMessage()
        }
    }

    Scaffold(
        topBar = {
            ChatScreenTopBar(
                title = screenTopBarTitle,
                onClickMoreMenu = {
                    when (chat) {
                        is Chat.C2C -> {
                            navHostController.navigate(
                                route = Screen.FriendProfileScreen.generateRoute(friendId = chat.id)
                            )
                        }
                        is Chat.Group -> {
                            navHostController.navigate(
                                route = Screen.GroupProfileScreen.generateRoute(groupId = chat.id)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatScreenBottomBar(
                sendText = {
                    chatViewModel.sendTextMessage(it)
                }, sendImage = {
                    chatViewModel.sendImageMessage(it)
                })
        }
    ) { contentPadding ->
        MessageScreen(
            listState = listState,
            chat = chat,
            contentPadding = contentPadding,
            chatScreenState = chatScreenState,
            onClickAvatar = onClickAvatar,
            onClickMessage = onClickMessage,
            onLongClickMessage = onLongClickMessage
        )
    }
}