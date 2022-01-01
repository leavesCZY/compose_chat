package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.base.model.Chat
import github.leavesc.compose_chat.extend.viewModelInstance
import github.leavesc.compose_chat.logic.ChatViewModel
import github.leavesc.compose_chat.model.Screen
import kotlinx.coroutines.flow.filter

/**
 * @Author: leavesC
 * @Date: 2021/7/3 23:53
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ChatScreen(
    navController: NavHostController,
    listState: LazyListState,
    chat: Chat
) {
    val chatViewModel = viewModelInstance {
        ChatViewModel(chat = chat)
    }

    val screenTopBarTitle by chatViewModel.screenTopBarTitle.collectAsState()
    val chatScreenState by chatViewModel.chatScreenState.collectAsState()

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
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primaryVariant)
            .statusBarsPadding(),
        topBar = {
            ChatScreenTopBar(
                navController = navController,
                title = screenTopBarTitle,
                onClickMoreMenu = {
                    when (chat) {
                        is Chat.C2C -> {
                            navController.navigate(
                                route = Screen.FriendProfileScreen.generateRoute(friendId = chat.id)
                            )
                        }
                        is Chat.Group -> {
                            navController.navigate(
                                route = Screen.GroupProfileScreen.generateRoute(groupId = chat.id)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatScreenBottomBord(
                sendMessage = {
                    chatViewModel.sendTextMessage(it)
                }, sendImage = {
                    chatViewModel.sendImageMessage(it)
                })
        }
    ) { contentPadding ->
        MessageScreen(
            navController = navController,
            listState = listState,
            contentPadding = contentPadding,
            chatScreenState = chatScreenState,
            chat = chat
        )
    }
}