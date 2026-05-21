package github.leavesczy.compose_chat.ui.chat.group.logic

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.proxy.GroupProvider
import github.leavesczy.compose_chat.ui.MainActivity
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.utils.randomImage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class GroupProfileViewModel(private val groupId: String) : BaseViewModel() {

    private val groupProvider: IGroupProvider = GroupProvider()

    var pageViewState by mutableStateOf(
        value = GroupProfilePageViewState(
            groupProfile = null,
            memberList = persistentListOf(),
            onClickMember = ::onClickMember,
            onClickSwitchAvatar = ::onClickSwitchAvatar,
            onClickQuitGroup = ::onClickQuitGroup
        )
    )
        private set

    init {
        viewModelScope.launch {
            showLoadingDialog()
            val groupProfileAsync = async {
                groupProvider.getGroupInfo(groupId = groupId)
            }
            val memberListAsync = async {
                groupProvider.getGroupMemberList(groupId = groupId)
            }
            val groupProfile = groupProfileAsync.await()
            val memberList = memberListAsync.await()
            if (groupProfile != null) {
                pageViewState = pageViewState.copy(
                    groupProfile = groupProfile,
                    memberList = memberList.toPersistentList()
                )
            }
            dismissLoadingDialog()
        }
    }

    private fun getGroupProfile() {
        viewModelScope.launch {
            groupProvider.getGroupInfo(groupId = groupId)?.let { groupProfile ->
                pageViewState = pageViewState.copy(groupProfile = groupProfile)
            }
        }
    }

    private fun onClickMember(
        activity: Activity,
        memberProfile: GroupMemberProfile
    ) {
        FriendProfileActivity.navTo(
            context = activity,
            friendId = memberProfile.detail.id
        )
    }

    private fun onClickSwitchAvatar() {
        viewModelScope.launch {
            showLoadingDialog()
            val avatarUrl = randomImage()
            val result = groupProvider.setAvatar(
                groupId = groupId,
                avatarUrl = avatarUrl
            )
            when (result) {
                ActionResult.Success -> {
                    getGroupProfile()
                    showToast(resId = R.string.toast_modify_success)
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
            dismissLoadingDialog()
        }
    }

    private fun onClickQuitGroup(activity: Activity) {
        viewModelScope.launch {
            showLoadingDialog()
            when (val result = groupProvider.quitGroup(groupId = groupId)) {
                ActionResult.Success -> {
                    showToast(resId = R.string.toast_quit_group_success)
                    activity.startActivity<MainActivity>()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
            dismissLoadingDialog()
        }
    }

}