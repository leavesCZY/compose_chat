package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IFriendshipProvider {

    val friendList: SharedFlow<List<PersonProfile>>

    fun refreshFriendList()

    suspend fun getFriendProfile(friendId: String): PersonProfile?

    suspend fun setFriendRemark(friendId: String, remark: String): ActionResult

    suspend fun addFriend(friendId: String): ActionResult

    suspend fun deleteFriend(friendId: String): ActionResult

}