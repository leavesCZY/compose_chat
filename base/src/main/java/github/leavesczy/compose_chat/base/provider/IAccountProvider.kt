package github.leavesczy.compose_chat.base.provider

import android.app.Application
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.base.model.ServerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IAccountProvider {

    val personProfile: StateFlow<PersonProfile>

    val serverConnectState: SharedFlow<ServerState>

    fun init(application: Application)

    suspend fun login(userId: String): ActionResult

    suspend fun logout(): ActionResult

    suspend fun getPersonProfile(): PersonProfile?

    fun refreshPersonProfile()

    suspend fun updatePersonProfile(
        faceUrl: String, nickname: String, signature: String
    ): ActionResult

}