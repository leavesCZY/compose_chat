package github.leavesczy.compose_chat.common.provider

import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.PersonProfile
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/6/27 15:49
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IFriendshipProvider {

    val friendList: SharedFlow<List<PersonProfile>>

    fun getFriendList()

    suspend fun getFriendProfile(friendId: String): PersonProfile?

    suspend fun setFriendRemark(friendId: String, remark: String): ActionResult

    suspend fun addFriend(friendId: String): ActionResult

    suspend fun deleteFriend(friendId: String): ActionResult

}