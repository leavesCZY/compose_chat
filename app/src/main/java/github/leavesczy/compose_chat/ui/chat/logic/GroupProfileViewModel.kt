package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.proxy.GroupProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileViewModel(private val groupId: String) : BaseViewModel() {

    private val groupProvider: IGroupProvider = GroupProvider()

    val pageViewState by mutableStateOf(
        value = GroupProfilePageViewState(
            groupProfile = mutableStateOf(value = null),
            memberList = mutableStateOf(value = emptyList())
        )
    )

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    init {
        viewModelScope.launch {
            loadingDialog(visible = true)
            val groupProfileAsync = async {
                groupProvider.getGroupInfo(groupId = groupId)
            }
            val memberListAsync = async {
                groupProvider.getGroupMemberList(groupId = groupId)
            }
            val groupProfile = groupProfileAsync.await()
            val memberList = memberListAsync.await()
            if (groupProfile != null) {
                pageViewState.groupProfile.value = groupProfile
                pageViewState.memberList.value = memberList
            }
            loadingDialog(visible = false)
        }
    }

    private fun getGroupProfile() {
        viewModelScope.launch {
            groupProvider.getGroupInfo(groupId = groupId)?.let {
                pageViewState.groupProfile.value = it
            }
        }
    }

    suspend fun quitGroup(): ActionResult {
        return groupProvider.quitGroup(groupId = groupId)
    }

    fun setAvatar(avatarUrl: String) {
        viewModelScope.launch {
            when (val result =
                groupProvider.setAvatar(groupId = groupId, avatarUrl = avatarUrl)) {
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

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}