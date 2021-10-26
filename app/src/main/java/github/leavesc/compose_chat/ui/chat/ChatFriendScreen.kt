package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.extend.navigate
import github.leavesc.compose_chat.logic.ChatFriendViewModel
import github.leavesc.compose_chat.model.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/3 23:53
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ChatFriendScreen(
    navController: NavHostController,
    listState: LazyListState,
    friendId: String
) {
    val chatC2CViewModel = viewModel<ChatFriendViewModel>(factory = object :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatFriendViewModel(friendId) as T
        }
    })

    val chatScreenState by chatC2CViewModel.chatFriendScreenState.collectAsState()

    if (chatScreenState.mushScrollToBottom) {
        LaunchedEffect(key1 = chatScreenState.mushScrollToBottom) {
            if (chatScreenState.mushScrollToBottom) {
                listState.animateScrollToItem(index = 0)
                chatC2CViewModel.alreadyScrollToBottom()
            }
        }
    }

    LaunchedEffect(listState) {
        launch {
            snapshotFlow {
                listState.firstVisibleItemIndex
            }.filter {
                !chatScreenState.loadFinish
            }.collect {
                chatC2CViewModel.loadMoreMessage()
            }
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
                title = chatScreenState.friendProfile.showName,
                onClickMoreMenu = {
                    val userId = chatScreenState.friendProfile.userId
                    if (userId.isNotBlank()) {
                        navController.navigate(
                            screen = Screen.FriendProfileScreen(friendId = userId)
                        )
                    }
                }
            )
        },
        bottomBar = {
            ChatScreenBottomBord(
                sendMessage = {
                    chatC2CViewModel.sendMessage(it)
                })
        }
    ) { contentPadding ->
        MessageScreen(
            navController = navController,
            chatFriendScreenState = chatScreenState,
            listState = listState,
            contentPadding = contentPadding,
        )
    }
}