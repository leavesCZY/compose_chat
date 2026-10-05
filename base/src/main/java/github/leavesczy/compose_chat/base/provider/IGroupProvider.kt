package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import kotlinx.coroutines.flow.StateFlow

interface IGroupProvider {

    val joinedGroupListFlow: StateFlow<List<GroupProfile>>

    val joinedGroupListLoadErrorFlow: StateFlow<String?>

    val joinedGroupListLoadingFlow: StateFlow<Boolean>

    fun refreshJoinedGroupList()

    fun clearSessionState()

    suspend fun joinGroup(groupId: String): ActionResult

    suspend fun quitGroup(groupId: String): ActionResult

    suspend fun getGroupInfo(groupId: String): GroupProfile?

    suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile>?

    suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult

}