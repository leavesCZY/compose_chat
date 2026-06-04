package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
interface IGroupProvider {

    val joinedGroupListFlow: SharedFlow<List<GroupProfile>>

    fun refreshJoinedGroupList()

    suspend fun joinGroup(groupId: String): ActionResult

    suspend fun quitGroup(groupId: String): ActionResult

    suspend fun getGroupInfo(groupId: String): GroupProfile?

    suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile>

    suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult

}