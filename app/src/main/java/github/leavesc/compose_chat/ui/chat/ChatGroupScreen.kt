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
import github.leavesc.compose_chat.logic.ChatGroupViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/10/27 0:04
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ChatGroupScreen(
    navController: NavHostController,
    listState: LazyListState,
    groupId: String
) {
    val chatGroupViewModel = viewModel<ChatGroupViewModel>(factory = object :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatGroupViewModel(groupId) as T
        }
    })

    val chatScreenState by chatGroupViewModel.chatGroupScreenState.collectAsState()

    if (chatScreenState.mushScrollToBottom) {
        LaunchedEffect(key1 = chatScreenState.mushScrollToBottom) {
            if (chatScreenState.mushScrollToBottom) {
                listState.animateScrollToItem(index = 0)
                chatGroupViewModel.alreadyScrollToBottom()
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
                chatGroupViewModel.loadMoreMessage()
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
                title = chatScreenState.groupProfile.name,
                onClickMoreMenu = {
                    val groupId = chatScreenState.groupProfile.id
                    if (groupId.isNotBlank()) {

                    }
                }
            )
        },
        bottomBar = {
            ChatScreenBottomBord(
                sendMessage = {
                    chatGroupViewModel.sendMessage(it)
                })
        }
    ) { contentPadding ->
        MessageGroupScreen(
            navController = navController,
            chatFriendScreenState = chatScreenState,
            listState = listState,
            contentPadding = contentPadding,
        )
    }
}