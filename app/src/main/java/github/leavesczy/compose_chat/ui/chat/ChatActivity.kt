package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.IntentCompat
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel

class ChatActivity : BaseActivity() {

    companion object {

        private const val KEY_CHAT = "keyChat"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(KEY_CHAT, chat)
            // standard + CLEAR_TOP：结束栈顶旧 Chat 并重建，避免错会话
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val chat: Chat? by lazy {
        IntentCompat.getParcelableExtra(intent, KEY_CHAT, Chat::class.java)
    }

    private val chatViewModel by viewModelsInstance {
        ChatViewModel(chat = requireNotNull(chat))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (chat == null) {
            finish()
            return
        }
        setContent {
            ChatPage(
                pageViewState = chatViewModel.pageViewState,
                bottomBarViewState = chatViewModel.bottomBarViewState,
                loadMessageViewState = chatViewModel.loadMessageViewState
            )
        }
    }

}