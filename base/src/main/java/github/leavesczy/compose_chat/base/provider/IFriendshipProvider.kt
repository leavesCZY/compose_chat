package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.PersonProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/6/27 15:49
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IFriendshipProvider {

    val friendList: StateFlow<List<PersonProfile>>

    fun getFriendList()

    suspend fun getFriendProfile(friendId: String): PersonProfile?

    suspend fun setFriendRemark(friendId: String, remark: String): ActionResult

    suspend fun addFriend(friendId: String): ActionResult

    suspend fun deleteFriend(friendId: String): ActionResult

}