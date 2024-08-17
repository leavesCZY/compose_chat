package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatActivity : BaseActivity() {

    companion object {

        private const val KEY_CHAT = "keyChat"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(KEY_CHAT, chat)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val chat: Chat by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(KEY_CHAT)!!
    }

    private val chatViewModel by viewModelsInstance {
        ChatViewModel(chat = chat)
    }

    private val chatPageAction = ChatPageAction(
        onClickAvatar = {
            val messageSenderId = it.detail.sender.id
            if (messageSenderId.isNotBlank()) {
                FriendProfileActivity.navTo(context = this, friendId = messageSenderId)
            }
        },
        onClickMessage = {
            when (it) {
                is ImageMessage -> {
                    val allImageUrl = chatViewModel.filterAllImageMessageUrl()
                    val initialImageUrl = it.previewImageUrl
                    if (allImageUrl.isNotEmpty() && initialImageUrl.isNotBlank()) {
                        val initialPage = allImageUrl.indexOf(element = initialImageUrl)
                            .coerceAtLeast(minimumValue = 0)
                        PreviewImageActivity.navTo(
                            context = this,
                            imageUriList = allImageUrl,
                            initialPage = initialPage
                        )
                    }
                }

                is TextMessage, is SystemMessage, is TimeMessage -> {

                }
            }
        },
        onLongClickMessage = {

        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatPage(
                chatViewModel = chatViewModel,
                chatPageAction = chatPageAction
            )
        }
    }

}

@Composable
private fun ChatPage(chatViewModel: ChatViewModel, chatPageAction: ChatPageAction) {
    val chatPageViewState = chatViewModel.chatPageViewState
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = chatPageViewState.topBarTitle.value,
                chat = chatPageViewState.chat
            )
        },
        bottomBar = {
            ChatPageBottomBar(chatViewModel = chatViewModel)
        }
    ) { innerPadding ->
        val refreshing by chatViewModel.loadMessageViewState.refreshing
        val loadFinish by chatViewModel.loadMessageViewState.loadFinish
        val pullRefreshState = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = {
                chatViewModel.loadMoreMessage()
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .pullRefresh(
                    state = pullRefreshState,
                    enabled = !loadFinish
                )
        ) {
            MessagePanel(
                pageViewState = chatPageViewState,
                chatPageAction = chatPageAction
            )
            PullRefreshIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter),
                refreshing = refreshing,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}