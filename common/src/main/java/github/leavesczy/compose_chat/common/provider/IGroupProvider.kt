package github.leavesczy.compose_chat.common.provider

import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.GroupMemberProfile
import github.leavesczy.compose_chat.common.model.GroupProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/7/12 0:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IGroupProvider {

    val joinedGroupList: StateFlow<List<GroupProfile>>

    fun getJoinedGroupList()

    suspend fun joinGroup(groupId: String): ActionResult

    suspend fun quitGroup(groupId: String): ActionResult

    suspend fun getGroupInfo(groupId: String): GroupProfile?

    suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile>

    suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult

    suspend fun transferGroupOwner(groupId: String, newOwnerUserID: String): ActionResult

}