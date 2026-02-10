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

    var pageViewState by mutableStateOf(
        value = FriendProfilePageViewState(
            personProfile = null,
            itIsMe = false,
            isFriend = false,
            showSetFriendRemarkPanel = ::showSetFriendRemarkPanel,
            addFriend = ::addFriend
        )
    )
        private set

    var setFriendRemarkDialogViewState by mutableStateOf(
        value = SetFriendRemarkDialogViewState(
            visible = false,
            remark = "",
            dismissDialog = ::dismissSetFriendRemarkDialog,
            setFriendRemark = ::setFriendRemark
        )
    )
        private set

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
                pageViewState = pageViewState.copy(personProfile = null)
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
                pageViewState = pageViewState.copy(
                    personProfile = profile,
                    itIsMe = itIsMe,
                    isFriend = isFriend
                )
                setFriendRemarkDialogViewState = setFriendRemarkDialogViewState.copy(
                    visible = false,
                    remark = profile.remark
                )
            }
            loadingDialog(visible = false)
        }
    }

    private fun addFriend() {
        viewModelScope.launch {
            when (val result = friendshipProvider.addFriend(friendId = friendId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400L)
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
        setFriendRemarkDialogViewState = setFriendRemarkDialogViewState.copy(visible = true)
    }

    private fun dismissSetFriendRemarkDialog() {
        setFriendRemarkDialogViewState = setFriendRemarkDialogViewState.copy(visible = false)
    }

    private fun setFriendRemark(remark: String) {
        viewModelScope.launch {
            when (val result =
                friendshipProvider.setFriendRemark(friendId = friendId, remark = remark)) {
                is ActionResult.Success -> {
                    setFriendRemarkDialogViewState =
                        setFriendRemarkDialogViewState.copy(remark = remark)
                    delay(timeMillis = 300L)
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