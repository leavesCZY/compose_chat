package github.leavesczy.compose_chat.ui.friendship.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.PersonProfile
import github.leavesczy.compose_chat.model.FriendProfilePageViewState
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/3 19:54
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendProfileViewModel(private val friendId: String) : ViewModel() {

    private val _friendProfilePageState = MutableStateFlow(
        FriendProfilePageViewState(
            personProfile = PersonProfile.Empty,
            showAlterBtb = false,
            showAddBtn = false
        )
    )

    val friendProfilePageState: StateFlow<FriendProfilePageViewState> = _friendProfilePageState

    fun getFriendProfile() {
        viewModelScope.launch {
            val profile =
                ComposeChat.friendshipProvider.getFriendProfile(friendId) ?: PersonProfile.Empty
            val showAlterBtb = profile.isFriend
            val showAddBtn = if (showAlterBtb) {
                false
            } else {
                val selfId = ComposeChat.accountProvider.personProfile.value.id
                selfId.isNotBlank() && selfId != friendId
            }
            _friendProfilePageState.emit(
                FriendProfilePageViewState(
                    personProfile = profile,
                    showAlterBtb = showAlterBtb,
                    showAddBtn = showAddBtn
                )
            )
        }
    }

    fun addFriend() {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast("添加成功")
                    getFriendProfile()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun deleteFriend() {
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

    fun setFriendRemark(remark: String) {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.setFriendRemark(
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

}