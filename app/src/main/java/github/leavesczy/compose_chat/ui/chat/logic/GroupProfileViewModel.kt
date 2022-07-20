package github.leavesczy.compose_chat.ui.chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.GroupProfile
import github.leavesczy.compose_chat.model.GroupProfilePageViewState
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/10/27 18:17
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileViewModel(private val groupId: String) : ViewModel() {

    private val _groupProfilePageViewState = MutableStateFlow(
        GroupProfilePageViewState(
            groupProfile = GroupProfile.Empty,
            memberList = emptyList()
        )
    )

    val groupProfilePageViewState: StateFlow<GroupProfilePageViewState> = _groupProfilePageViewState

    init {
        getGroupProfile()
        getGroupMemberList()
    }

    private fun getGroupProfile() {
        viewModelScope.launch {
            ComposeChat.groupProvider.getGroupInfo(groupId = groupId)?.let {
                _groupProfilePageViewState.emit(
                    value = _groupProfilePageViewState.value.copy(
                        groupProfile = it
                    )
                )
            }
        }
    }

    private fun getGroupMemberList() {
        viewModelScope.launch {
            val memberList = ComposeChat.groupProvider.getGroupMemberList(groupId = groupId)
            _groupProfilePageViewState.emit(value = _groupProfilePageViewState.value.copy(memberList = memberList))
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