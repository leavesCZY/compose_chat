package github.leavesczy.compose_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.extend.viewModelInstance
import github.leavesczy.compose_chat.model.ChatPageAction
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.friendship.FriendProfileActivity
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: CZY
 * @Date: 2022/7/17 14:04
 * @Desc:
 */
class ChatActivity : BaseActivity() {

    companion object {

        private const val keyType = "keyType"

        private const val keyId = "keyId"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(keyType, chat.type)
            intent.putExtra(keyId, chat.id)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }

    }

    private val chat by lazy {
        val type = intent.getIntExtra(keyType, 0)
        val id = intent.getStringExtra(keyId) ?: ""
        Chat.find(type = type, id = id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val chatViewModel = viewModelInstance {
                ChatViewModel(chat = chat)
            }
            val chatPageState by chatViewModel.chatPageViewState.collectAsState()
            val clipboardManager = LocalClipboardManager.current
            val chatPageAction = remember {
                ChatPageAction(
                    onClickBackMenu = {
                        finish()
                    },
                    onClickMoreMenu = {
                        when (chat) {
                            is PrivateChat -> {
                                FriendProfileActivity.navTo(context = this, friendId = chat.id)
                            }
                            is GroupChat -> {
                                GroupProfileActivity.navTo(context = this, groupId = chat.id)
                            }
                        }
                    },
                    sendTextMessage = {
                        chatViewModel.sendTextMessage(text = it)
                    },
                    sendImageMessage = {
                        chatViewModel.sendImageMessage(mediaResources = it)
                    },
                    loadMoreMessage = {
                        chatViewModel.loadMoreMessage()
                    },
                    onClickAvatar = {
                        val messageSenderId = it.messageDetail.sender.id
                        if (messageSenderId.isNotBlank()) {
                            FriendProfileActivity.navTo(context = this, friendId = messageSenderId)
                        }
                    },
                    onClickMessage = {
                        when (it) {
                            is ImageMessage -> {
                                val imagePath = it.previewUrl
                                if (imagePath.isBlank()) {
                                    showToast("??????????????????")
                                } else {
                                    PreviewImageActivity.navTo(
                                        context = this,
                                        imagePath = imagePath
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
                                    clipboardManager.setText(AnnotatedString(msg))
                                    showToast("?????????")
                                }
                            }
                            else -> {

                            }
                        }
                    }
                )
            }
            LaunchedEffect(key1 = "mushScrollToBottom", block = {
                launch {
                    chatViewModel.mushScrollToBottom.collect {
                        chatPageState.listState.animateScrollToItem(index = 0, scrollOffset = 0)
                    }
                }
            })
            ComposeChatTheme {
                ChatPage(
                    chatPageViewState = chatPageState,
                    chatPageAction = chatPageAction
                )
            }
        }
    }

}