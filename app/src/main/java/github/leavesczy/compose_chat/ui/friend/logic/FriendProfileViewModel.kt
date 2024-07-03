package github.leavesczy.compose_chat.ui.friend.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.proxy.FriendshipProvider
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

    private val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val pageViewState by mutableStateOf(
        value = FriendProfilePageViewState(
            personProfile = mutableStateOf(value = null),
            itIsMe = mutableStateOf(value = false),
            isFriend = mutableStateOf(value = false),
            showSetFriendRemarkPanel = ::showSetFriendRemarkPanel,
            addFriend = ::addFriend
        )
    )

    val setFriendRemarkDialogViewState by mutableStateOf(
        value = SetFriendRemarkDialogViewState(
            visible = mutableStateOf(value = false),
            remark = mutableStateOf(value = ""),
            dismissDialog = ::dismissSetFriendRemarkDialog,
            setFriendRemark = ::setFriendRemark
        )
    )

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    init {
        getFriendProfile()
    }

    private fun getFriendProfile() {
        viewModelScope.launch {
            loadingDialog(visible = true)
            val profile = friendshipProvider.getFriendProfile(friendId = friendId)
            if (profile == null) {
                pageViewState.personProfile.value = null
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
                pageViewState.personProfile.value = profile
                pageViewState.itIsMe.value = itIsMe
                pageViewState.isFriend.value = isFriend
                setFriendRemarkDialogViewState.visible.value = false
                setFriendRemarkDialogViewState.remark.value = profile.remark
            }
            loadingDialog(visible = false)
        }
    }

    private fun addFriend() {
        viewModelScope.launch {
            when (val result = friendshipProvider.addFriend(friendId = friendId)) {
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
            friendshipProvider.deleteFriend(friendId = friendId)) {
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
        setFriendRemarkDialogViewState.visible.value = true
    }

    private fun dismissSetFriendRemarkDialog() {
        setFriendRemarkDialogViewState.visible.value = false
    }

    private fun setFriendRemark(remark: String) {
        viewModelScope.launch {
            when (val result =
                friendshipProvider.setFriendRemark(friendId = friendId, remark = remark)) {
                is ActionResult.Success -> {
                    setFriendRemarkDialogViewState.remark.value = remark
                    delay(timeMillis = 300)
                    getFriendProfile()
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