package github.leavesczy.compose_chat.ui.chat.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.IntentCompat
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatViewModel
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

    private val chat by lazy(mode = LazyThreadSafetyMode.NONE) {
        IntentCompat.getParcelableExtra(intent, KEY_CHAT, Chat::class.java)!!
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