package github.leavesczy.compose_chat.ui.friendship.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.main.logic.FriendProfilePageViewState
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendProfileViewModel(private val friendId: String) : ViewModel() {

    var friendProfilePageState by mutableStateOf<FriendProfilePageViewState?>(value = null)
        private set

    var setFriendRemarkDialogViewState by mutableStateOf<SetFriendRemarkDialogViewState?>(value = null)
        private set

    private val _friendProfilePageAction = MutableSharedFlow<FriendProfilePageAction>()

    val friendProfilePageAction: SharedFlow<FriendProfilePageAction> = _friendProfilePageAction

    init {
        getFriendProfile()
    }

    private fun getFriendProfile() {
        viewModelScope.launch {
            val profile = ComposeChat.friendshipProvider.getFriendProfile(friendId = friendId)
            if (profile == null) {
                friendProfilePageState = null
            } else {
                val itIsMe = kotlin.run {
                    val selfId = ComposeChat.accountProvider.personProfile.value.id
                    selfId.isBlank() || selfId == friendId
                }
                val isFriend = if (itIsMe) {
                    false
                } else {
                    profile.isFriend
                }
                friendProfilePageState = FriendProfilePageViewState(
                    personProfile = profile,
                    itIsMe = itIsMe,
                    isFriend = isFriend
                )
                setFriendRemarkDialogViewState = SetFriendRemarkDialogViewState(
                    visible = false,
                    personProfile = profile
                )
            }
        }
    }

    fun addFriend() {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    getFriendProfile()
                    showToast(msg = "添加成功")
                }
                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    fun deleteFriend() {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.deleteFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    showToast(msg = "已删除好友")
                    finishActivity()
                }
                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    fun showSetFriendRemarkPanel() {
        val mSetFriendRemarkDialogViewState = setFriendRemarkDialogViewState
        if (mSetFriendRemarkDialogViewState != null) {
            setFriendRemarkDialogViewState = mSetFriendRemarkDialogViewState.copy(visible = true)
        }
    }

    fun dismissSetFriendRemarkDialog() {
        val mSetFriendRemarkDialogViewState = setFriendRemarkDialogViewState
        if (mSetFriendRemarkDialogViewState != null) {
            setFriendRemarkDialogViewState = mSetFriendRemarkDialogViewState.copy(visible = false)
        }
    }

    fun setFriendRemark(remark: String) {
        viewModelScope.launch {
            when (val result = ComposeChat.friendshipProvider.setFriendRemark(
                friendId = friendId,
                remark = remark
            )) {
                is ActionResult.Success -> {
                    delay(timeMillis = 300)
                    getFriendProfile()
                    ComposeChat.friendshipProvider.refreshFriendList()
                    ComposeChat.conversationProvider.refreshConversationList()
                    dismissSetFriendRemarkDialog()
                }
                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    fun navToChat() {
        viewModelScope.launch {
            _friendProfilePageAction.emit(value = FriendProfilePageAction.NavToChat)
        }
    }

    private fun finishActivity() {
        viewModelScope.launch {
            _friendProfilePageAction.emit(value = FriendProfilePageAction.FinishActivity)
        }
    }

}