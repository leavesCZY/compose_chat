package github.leavesczy.compose_chat.base.provider

import android.app.Application
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.ServerConnectState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IAccountProvider {

    val personProfileFlow: StateFlow<PersonProfile>

    val serverConnectStateFlow: SharedFlow<ServerConnectState>

    fun init(application: Application)

    suspend fun login(userId: String): ActionResult

    suspend fun logout(): ActionResult

    suspend fun getPersonProfile(): PersonProfile?

    suspend fun refreshPersonProfile()

    suspend fun updateProfile(
        avatarUrl: String,
        nickname: String,
        signature: String
    ): ActionResult

}