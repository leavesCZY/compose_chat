package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.GroupMemberProfile
import github.leavesczy.compose_chat.base.model.GroupProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IGroupProvider {

    val joinedGroupList: StateFlow<List<GroupProfile>>

    fun refreshJoinedGroupList()

    suspend fun joinGroup(groupId: String): ActionResult

    suspend fun quitGroup(groupId: String): ActionResult

    suspend fun getGroupInfo(groupId: String): GroupProfile?

    suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile>

    suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult

    suspend fun transferGroupOwner(groupId: String, newOwnerUserID: String): ActionResult

    suspend fun clear()

}