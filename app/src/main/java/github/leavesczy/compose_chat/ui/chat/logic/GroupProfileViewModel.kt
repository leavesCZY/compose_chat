package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileViewModel(private val groupId: String) : ViewModel() {

    var groupProfilePageViewState by mutableStateOf(
        value = GroupProfilePageViewState(
            groupProfile = GroupProfile.Empty, memberList = emptyList()
        )
    )
        private set

    init {
        viewModelScope.launch {
            val groupProfileAsync = async {
                ComposeChat.groupProvider.getGroupInfo(groupId = groupId) ?: GroupProfile.Empty
            }
            val memberListAsync = async {
                ComposeChat.groupProvider.getGroupMemberList(groupId = groupId)
            }
            groupProfilePageViewState = groupProfilePageViewState.copy(
                groupProfile = groupProfileAsync.await(), memberList = memberListAsync.await()
            )
        }
    }

    private fun getGroupProfile() {
        viewModelScope.launch {
            ComposeChat.groupProvider.getGroupInfo(groupId = groupId)?.let {
                groupProfilePageViewState = groupProfilePageViewState.copy(
                    groupProfile = it
                )
            }
        }
    }

    suspend fun quitGroup(): ActionResult {
        return ComposeChat.groupProvider.quitGroup(groupId = groupId)
    }

    fun setAvatar(avatarUrl: String) {
        viewModelScope.launch {
            when (val result = ComposeChat.groupProvider.setAvatar(
                groupId = groupId, avatarUrl = avatarUrl
            )) {
                ActionResult.Success -> {
                    getGroupProfile()
                    showToast(msg = "修改成功")
                }
                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

}