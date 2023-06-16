package github.leavesczy.compose_chat.ui.friend.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendProfileViewModel(private val friendId: String) : BaseViewModel() {

    var pageViewState by mutableStateOf<FriendProfilePageViewState?>(value = null)
        private set

    var setFriendRemarkDialogViewState by mutableStateOf<SetFriendRemarkDialogViewState?>(value = null)
        private set

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    init {
        getFriendProfile()
    }

    private fun getFriendProfile() {
        viewModelScope.launch {
            loadingDialog(visible = true)
            val profile = ComposeChat.friendshipProvider.getFriendProfile(friendId = friendId)
            if (profile == null) {
                pageViewState = null
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
                pageViewState = FriendProfilePageViewState(
                    personProfile = profile,
                    itIsMe = itIsMe,
                    isFriend = isFriend,
                    showSetFriendRemarkPanel = ::showSetFriendRemarkPanel,
                    addFriend = ::addFriend
                )
                setFriendRemarkDialogViewState = SetFriendRemarkDialogViewState(
                    visible = false,
                    personProfile = profile,
                    dismissSetFriendRemarkDialog = ::dismissSetFriendRemarkDialog,
                    setFriendRemark = ::setFriendRemark
                )
            }
            loadingDialog(visible = false)
        }
    }

    private fun addFriend() {
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

    suspend fun deleteFriend(): Boolean {
        return when (val result =
            ComposeChat.friendshipProvider.deleteFriend(friendId = friendId)) {
            is ActionResult.Success -> {
                showToast(msg = "已删除好友")
                true
            }

            is ActionResult.Failed -> {
                showToast(msg = result.reason)
                false
            }
        }
    }

    private fun showSetFriendRemarkPanel() {
        val mSetFriendRemarkDialogViewState = setFriendRemarkDialogViewState
        if (mSetFriendRemarkDialogViewState != null) {
            setFriendRemarkDialogViewState = mSetFriendRemarkDialogViewState.copy(visible = true)
        }
    }

    private fun dismissSetFriendRemarkDialog() {
        val mSetFriendRemarkDialogViewState = setFriendRemarkDialogViewState
        if (mSetFriendRemarkDialogViewState != null) {
            setFriendRemarkDialogViewState = mSetFriendRemarkDialogViewState.copy(visible = false)
        }
    }

    private fun setFriendRemark(remark: String) {
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

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}