package github.leavesczy.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.model.GroupProfileScreenState
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

    val groupProfileScreenState = MutableStateFlow(
        GroupProfileScreenState(
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
                groupProfileScreenState.emit(value = groupProfileScreenState.value.copy(groupProfile = it))
            }
        }
    }

    private fun getGroupMemberList() {
        viewModelScope.launch {
            val memberList = ComposeChat.groupProvider.getGroupMemberList(groupId = groupId)
            groupProfileScreenState.emit(value = groupProfileScreenState.value.copy(memberList = memberList))
        }
    }

    suspend fun quitGroup(): ActionResult {
        return ComposeChat.groupProvider.quitGroup(groupId = groupId)
    }

    fun deleteGroupConversation(groupId: String) {
        viewModelScope.launch {
            ComposeChat.conversationProvider.deleteGroupConversation(groupId = groupId)
        }
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