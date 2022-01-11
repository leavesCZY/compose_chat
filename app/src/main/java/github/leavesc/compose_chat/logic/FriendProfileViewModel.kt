package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.model.FriendProfileScreenState
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/3 19:54
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class FriendProfileViewModel(private val friendId: String) : ViewModel() {

    val friendProfileScreenState = MutableStateFlow(
        FriendProfileScreenState(
            personProfile = PersonProfile.Empty,
            showAlterBtb = false,
            showAddBtn = false
        )
    )

    fun getFriendProfile() {
        viewModelScope.launch {
            val profile =
                ComposeChat.friendshipProvider.getFriendProfile(friendId) ?: PersonProfile.Empty
            val showAlterBtb = profile.isFriend
            val showAddBtn = if (showAlterBtb) {
                false
            } else {
                val selfId = ComposeChat.accountProvider.personProfile.value.userId
                selfId.isNotBlank() && selfId != friendId
            }
            friendProfileScreenState.emit(
                FriendProfileScreenState(
                    personProfile = profile,
                    showAlterBtb = showAlterBtb,
                    showAddBtn = showAddBtn
                )
            )
        }
    }

    fun deleteFriend(friendId: String) {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.deleteFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    showToast("已删除好友")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun setFriendRemark(friendId: String, remark: String) {
        viewModelScope.launch {
            when (val result =
                ComposeChat.friendshipProvider.setFriendRemark(
                    friendId = friendId,
                    remark = remark
                )) {
                is ActionResult.Success -> {
                    showToast("设置成功")
                    getFriendProfile()
                    ComposeChat.friendshipProvider.getFriendList()
                    ComposeChat.conversationProvider.getConversationList()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun addFriend() {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    showToast("添加成功")
                    getFriendProfile()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

}