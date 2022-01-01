package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import github.leavesc.compose_chat.base.model.Chat
import github.leavesc.compose_chat.extend.navToPreviewImageScreen
import github.leavesc.compose_chat.model.ChatScreenState
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.utils.showToast

/**
 * @Author: leavesC
 * @Date: 2021/10/26 15:50
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun MessageScreen(
    navController: NavController,
    listState: LazyListState,
    contentPadding: PaddingValues,
    chatScreenState: ChatScreenState,
    chat: Chat,
) {
    val clipboardManager = LocalClipboardManager.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        state = listState,
        reverseLayout = true,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.Top,
    ) {
        for (message in chatScreenState.messageList) {
            item(key = message.messageDetail.msgId) {
                MessageItems(
                    message = message,
                    showSenderName = chat is Chat.Group,
                    onClickSelfAvatar = {
                        val messageSenderId = it.messageDetail.sender.userId
                        if (messageSenderId.isNotBlank()) {
                            navController.navigate(
                                route = Screen.FriendProfileScreen.generateRoute(friendId = messageSenderId)
                            )
                        }
                    },
                    onClickFriendAvatar = {
                        val messageSenderId = it.messageDetail.sender.userId
                        if (messageSenderId.isNotBlank()) {
                            navController.navigate(
                                route = Screen.FriendProfileScreen.generateRoute(friendId = messageSenderId)
                            )
                        }
                    },
                    onLongPressTextMessage = {
                        val msg = it.msg
                        if (msg.isNotEmpty()) {
                            clipboardManager.setText(AnnotatedString(msg))
                            showToast("已复制")
                        }
                    },
                    onClickImageMessage = {
                        val imagePath = it.imagePath
                        if (imagePath.isBlank()) {
                            showToast("图片路径为空")
                        } else {
                            navController.navToPreviewImageScreen(imagePath = imagePath)
                        }
                    }
                )
            }
        }
        if (chatScreenState.showLoadMore) {
            item {
                LoadMoreMessageItem()
            }
        }
    }
}