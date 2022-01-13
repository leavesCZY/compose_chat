package github.leavesczy.compose_chat.base.provider

import android.content.Context
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.base.model.ServerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/7/9 22:33
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IAccountProvider {

    val personProfile: StateFlow<PersonProfile>

    val serverConnectState: SharedFlow<ServerState>

    fun init(context: Context)

    suspend fun login(userId: String): ActionResult

    suspend fun logout(): ActionResult

    fun refreshPersonProfile()

    suspend fun updatePersonProfile(faceUrl: String, nickname: String, signature: String): Boolean

}