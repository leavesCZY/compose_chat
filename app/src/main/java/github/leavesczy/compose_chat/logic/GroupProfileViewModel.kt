package github.leavesczy.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.GroupProfile
import github.leavesczy.compose_chat.model.GroupProfilePageState
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/10/27 18:17
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileViewModel(private val groupId: String) : ViewModel() {

    val groupProfilePageState = MutableStateFlow(
        GroupProfilePageState(
            groupProfile = GroupProfile.Empty,
            memberList = emptyList()
        )
    )

    init {
        getGroupProfile()
        getGroupMemberList()
    }

    private fun getGroupProfile() {
        viewModelScope.launch {
            ComposeChat.groupProvider.getGroupInfo(groupId = groupId)?.let {
                groupProfilePageState.emit(value = groupProfilePageState.value.copy(groupProfile = it))
            }
        }
    }

    private fun getGroupMemberList() {
        viewModelScope.launch {
            val memberList = ComposeChat.groupProvider.getGroupMemberList(groupId = groupId)
            groupProfilePageState.emit(value = groupProfilePageState.value.copy(memberList = memberList))
        }
    }

    suspend fun quitGroup(): ActionResult {
        return ComposeChat.groupProvider.quitGroup(groupId = groupId)
    }

    fun setAvatar(avatarUrl: String) {
        viewModelScope.launch {
            when (val result =
                ComposeChat.groupProvider.setAvatar(groupId = groupId, avatarUrl = avatarUrl)) {
                ActionResult.Success -> {
                    getGroupProfile()
                    showToast("修改成功")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

}