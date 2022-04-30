package github.leavesczy.compose_chat.logic

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.cache.AccountCache
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.ImageUtils
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Author: leavesCZY
 * @Date: 2021/6/27 14:24
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class HomeViewModel : ViewModel() {

    val conversationList = ComposeChat.conversationProvider.conversationList

    val totalUnreadCount = ComposeChat.conversationProvider.totalUnreadMessageCount

    val fiendList = ComposeChat.friendshipProvider.friendList

    val joinedGroupList = ComposeChat.groupProvider.joinedGroupList

    val personProfile = ComposeChat.accountProvider.personProfile

    init {
        ComposeChat.conversationProvider.getConversationList()
        ComposeChat.conversationProvider.getTotalUnreadMessageCount()
        ComposeChat.friendshipProvider.getFriendList()
        ComposeChat.accountProvider.getPersonProfile()
        getJoinedGroupList()
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            val result = when (conversation) {
                is C2CConversation -> {
                    ComposeChat.conversationProvider.deleteC2CConversation(userId = conversation.id)
                }
                is GroupConversation -> {
                    ComposeChat.conversationProvider.deleteGroupConversation(groupId = conversation.id)
                }
            }
            when (result) {
                is ActionResult.Success -> {
                    ComposeChat.conversationProvider.getConversationList()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch {
            ComposeChat.conversationProvider.pinConversation(conversation = conversation, pin = pin)
        }
    }

    fun updateProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ) {
        viewModelScope.launch {
            val result = ComposeChat.accountProvider.updatePersonProfile(
                faceUrl = faceUrl,
                nickname = nickname,
                signature = signature
            )
            when (result) {
                is ActionResult.Success -> {
                    showToast("更新成功")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    suspend fun uploadImage(imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val imageFile =
                ImageUtils.saveImageToCacheDir(context = ContextHolder.context, imageUri = imageUri)
            val imagePath = imageFile?.absolutePath
            if (!imagePath.isNullOrBlank()) {
                return@withContext ComposeChat.messageProvider.uploadImage(
                    chat = GroupChat(id = ComposeChat.groupIdToUploadAvatar),
                    imagePath = imagePath
                )
            }
            return@withContext ""
        }
    }

    fun addFriend(userId: String) {
        val formatUserId = userId.lowercase()
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    showToast("添加成功")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun getJoinedGroupList() {
        ComposeChat.groupProvider.getJoinedGroupList()
    }

    suspend fun joinGroup(groupId: String): ActionResult {
        return ComposeChat.groupProvider.joinGroup(groupId)
    }

    fun logout() {
        viewModelScope.launch {
            when (val result = ComposeChat.accountProvider.logout()) {
                is ActionResult.Success -> {
                    AccountCache.onUserLogout()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

}