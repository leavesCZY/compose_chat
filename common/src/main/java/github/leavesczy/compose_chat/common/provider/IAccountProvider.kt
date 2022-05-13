package github.leavesczy.compose_chat.common.provider

import android.app.Application
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.PersonProfile
import github.leavesczy.compose_chat.common.model.ServerState
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

    fun init(application: Application)

    suspend fun login(userId: String): ActionResult

    suspend fun logout(): ActionResult

    fun getPersonProfile()

    suspend fun updatePersonProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ): ActionResult

}