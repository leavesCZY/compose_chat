package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ImageMessage
import github.leavesczy.compose_chat.base.model.TextMessage
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.SystemBarTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatActivity : BaseActivity() {

    companion object {

        private const val keyChat = "keyChat"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(keyChat, chat)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    @Suppress("DEPRECATION")
    private val chat: Chat by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(keyChat)!!
    }

    private val chatViewModel by viewModelsInstance {
        ChatViewModel(chat = chat)
    }

    private val chatPageAction = ChatPageAction(
        onClickAvatar = {
            val messageSenderId = it.messageDetail.sender.id
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
                            imageUrlList = allImageUrl,
                            initialPage = initialPage
                        )
                    }
                }

                else -> {

                }
            }
        },
        onLongClickMessage = {
            when (it) {
                is TextMessage -> {
                    val msg = it.formatMessage
                    if (msg.isNotEmpty()) {
                        copyText(context = this, text = msg)
                        showToast(msg = "已复制")
                    }
                }

                else -> {

                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(
            systemBarTheme = {
                SystemBarTheme(navigationBarColor = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        ) {
            ChatPage(
                chatViewModel = chatViewModel,
                chatPageAction = chatPageAction
            )
        }
    }

    private fun copyText(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboardManager != null) {
            val clipData = ClipData.newPlainText(context.getString(R.string.app_name), text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }

}

@Composable
private fun ChatPage(chatViewModel: ChatViewModel, chatPageAction: ChatPageAction) {
    val chatPageViewState = chatViewModel.chatPageViewState
    val loadMessageViewState = chatViewModel.loadMessageViewState
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = chatPageViewState.topBarTitle,
                chat = chatPageViewState.chat
            )
        },
        bottomBar = {
            ChatPageBottomBar(chatViewModel = chatViewModel)
        }
    ) { innerPadding ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = loadMessageViewState.refreshing,
            onRefresh = {
                chatViewModel.loadMoreMessage()
            })
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .pullRefresh(
                    state = pullRefreshState,
                    enabled = !loadMessageViewState.loadFinish
                )
        ) {
            MessagePanel(chatPageViewState = chatPageViewState, chatPageAction = chatPageAction)
            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = loadMessageViewState.refreshing,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}