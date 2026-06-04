package github.leavesczy.compose_chat.ui.friend.logic

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.BaseViewModel
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.ui.chat.main.ChatActivity
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
class FriendProfileViewModel(private val friendId: String) : BaseViewModel() {

    private val friendshipProvider: IFriendshipProvider = ComposeChat.friendshipProvider

    var pageViewState by mutableStateOf(
        value = FriendProfilePageViewState(
            personProfile = null,
            isMe = false,
            isFriend = false,
            onClickSetFriendRemark = ::showSetFriendRemarkPanel,
            onClickAddFriend = ::addFriend,
            onClickDeleteFriend = ::showDeleteFriendDialog,
            onClickChat = ::openChat
        )
    )
        private set

    var remarkDialogViewState by mutableStateOf(
        value = SetFriendRemarkDialogViewState(
            isVisible = false,
            remark = "",
            onDismissDialog = ::dismissSetFriendRemarkDialog,
            onSetFriendRemark = ::setFriendRemark
        )
    )
        private set

    var deleteFriendDialogViewState by mutableStateOf(
        value = DeleteFriendDialogViewState(
            isVisible = false,
            onDismissDialog = ::dismissDeleteFriendDialog,
            onDeleteFriend = ::confirmDeleteFriend
        )
    )
        private set

    init {
        getFriendProfile()
    }

    private fun getFriendProfile() {
        viewModelScope.launch {
            showLoadingDialog()
            val profile = friendshipProvider.getFriendProfile(friendId = friendId)
            if (profile == null) {
                pageViewState = pageViewState.copy(personProfile = null)
            } else {
                val selfId = ComposeChat.accountProvider.personProfileFlow.value.id
                val isMe = selfId.isBlank() || selfId == friendId
                val isFriend = if (isMe) {
                    false
                } else {
                    profile.isFriend
                }
                pageViewState = pageViewState.copy(
                    personProfile = profile,
                    isMe = isMe,
                    isFriend = isFriend
                )
                remarkDialogViewState = remarkDialogViewState.copy(
                    isVisible = false,
                    remark = profile.remark
                )
            }
            dismissLoadingDialog()
        }
    }

    private fun addFriend() {
        viewModelScope.launch {
            when (val result = friendshipProvider.addFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400L)
                    getFriendProfile()
                    showToast(resId = R.string.toast_add_friend_success)
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
        }
    }

    private suspend fun deleteFriend(): Boolean {
        return when (val result = friendshipProvider.deleteFriend(friendId = friendId)) {
            is ActionResult.Success -> {
                showToast(resId = R.string.toast_delete_friend_success)
                true
            }

            is ActionResult.Failed -> {
                showToast(msg = result.desc)
                false
            }
        }
    }

    private fun showSetFriendRemarkPanel() {
        remarkDialogViewState = remarkDialogViewState.copy(isVisible = true)
    }

    private fun dismissSetFriendRemarkDialog() {
        remarkDialogViewState = remarkDialogViewState.copy(isVisible = false)
    }

    private fun showDeleteFriendDialog() {
        deleteFriendDialogViewState = deleteFriendDialogViewState.copy(isVisible = true)
    }

    private fun dismissDeleteFriendDialog() {
        deleteFriendDialogViewState = deleteFriendDialogViewState.copy(isVisible = false)
    }

    private fun confirmDeleteFriend() {
        viewModelScope.launch {
            if (deleteFriend()) {
                dismissDeleteFriendDialog()
                (context as? Activity)?.finish()
            }
        }
    }

    private fun openChat() {
        ChatActivity.navTo(context = context, chat = Chat.C2C(id = friendId))
        (context as? Activity)?.finish()
    }

    private fun setFriendRemark(remark: String) {
        viewModelScope.launch {
            showLoadingDialog()
            val result = friendshipProvider.setFriendRemark(friendId = friendId, remark = remark)
            when (result) {
                is ActionResult.Success -> {
                    remarkDialogViewState = remarkDialogViewState.copy(remark = remark)
                    delay(timeMillis = 300L)
                    getFriendProfile()
                    dismissSetFriendRemarkDialog()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
            dismissLoadingDialog()
        }
    }

}
