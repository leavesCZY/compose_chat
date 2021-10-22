package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.logic.Chat
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/3 19:54
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class FriendProfileViewModel(private val friendId: String) : ViewModel() {

    val friendProfile = MutableStateFlow(PersonProfile.Empty)

    val onDeleteFriendSuccess = MutableStateFlow(false)

    fun getFriendProfile() {
        viewModelScope.launch(Dispatchers.Main) {
            friendProfile.emit(
                Chat.friendshipProvider.getFriendProfile(friendId) ?: PersonProfile.Empty
            )
        }
    }

    fun deleteFriend(friendId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = Chat.friendshipProvider.deleteFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    showToast("已删除好友")
                    onDeleteFriendSuccess.value = true
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun setFriendRemark(friendId: String, remark: String) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result =
                Chat.friendshipProvider.setFriendRemark(friendId = friendId, remark = remark)) {
                is ActionResult.Success -> {
                    showToast("设置成功")
                    getFriendProfile()
                    Chat.friendshipProvider.getFriendList()
                    Chat.conversationProvider.getConversationList()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

}