package github.leavesc.compose_chat.base.provider

import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.GroupMemberProfile
import github.leavesc.compose_chat.base.model.GroupProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesC
 * @Date: 2021/7/12 0:04
 * @Desc:
 * @Github：https://github.com/leavesC
 */
interface IGroupProvider {

    val joinedGroupList: StateFlow<List<GroupProfile>>

    fun getJoinedGroupList()

    suspend fun joinGroup(groupId: String): ActionResult

    suspend fun quitGroup(groupId: String): ActionResult

    suspend fun getGroupInfo(groupId: String): GroupProfile?

    suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile>

}