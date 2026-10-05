package github.leavesczy.compose_chat.proxy

import android.app.Application
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.base.utils.StringResources
import github.leavesczy.compose_chat.proxy.Converters.convertSelfProfile
import github.leavesczy.compose_chat.proxy.Converters.getSelfProfile
import github.leavesczy.compose_chat.proxy.Converters.getSelfProfileOrigin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

object AccountProvider : IAccountProvider {

    private val _personProfileFlow = MutableStateFlow(value = PersonProfile.Empty)

    override val personProfileFlow: StateFlow<PersonProfile> = _personProfileFlow.asStateFlow()

    private val _serverConnectStateFlow = MutableStateFlow(value = ServerConnectState.Idle)

    override val serverConnectStateFlow: StateFlow<ServerConnectState> =
        _serverConnectStateFlow.asStateFlow()

    private val generateUserSig =
        GenerateUserSig(appId = Constants.APP_ID, appSecretKey = Constants.APP_SECRET_KEY)

    override fun init(application: Application) {
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_NONE
        V2TIMManager.getInstance().addIMSDKListener(object : V2TIMSDKListener() {
            override fun onConnecting() {
                dispatchServerConnectState(connectState = ServerConnectState.Connecting)
            }

            override fun onConnectSuccess() {
                dispatchServerConnectState(connectState = ServerConnectState.Connected)
            }

            override fun onConnectFailed(code: Int, error: String) {
                dispatchServerConnectState(connectState = ServerConnectState.ConnectFailed)
            }

            override fun onUserSigExpired() {
                clearAllSessionState()
                dispatchServerConnectState(connectState = ServerConnectState.UserSigExpired)
            }

            override fun onKickedOffline() {
                clearAllSessionState()
                dispatchServerConnectState(connectState = ServerConnectState.KickedOffline)
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
                _personProfileFlow.value = convertSelfProfile(profile = info)
            }
        })
        V2TIMManager.getInstance().initSDK(application, Constants.APP_ID, config)
    }

    private fun dispatchServerConnectState(connectState: ServerConnectState) {
        _serverConnectStateFlow.value = connectState
    }

    override fun clearSessionState() {
        clearAllSessionState()
    }

    override suspend fun login(userId: String): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            clearAllSessionState()
            val formatUserId = userId.lowercase()
            val result = suspendTimCallback { callback ->
                V2TIMManager.getInstance().login(
                    formatUserId,
                    generateUserSig.genUserSig(userId = formatUserId),
                    callback
                )
            }
            if (result is ActionResult.Success) {
                dispatchServerConnectState(connectState = ServerConnectState.Connected)
            }
            result
        }
    }

    override suspend fun logout(): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            val result = suspendTimCallback { callback ->
                V2TIMManager.getInstance().logout(callback)
            }
            if (result is ActionResult.Success) {
                clearAllSessionState()
                dispatchServerConnectState(connectState = ServerConnectState.Logout)
            }
            result
        }
    }

    private fun clearAllSessionState() {
        _personProfileFlow.value = PersonProfile.Empty
        TimMessageStore.clear()
        MessageProvider.clearSessionState()
        ConversationProvider.clearSessionState()
        FriendshipProvider.clearSessionState()
        GroupProvider.clearSessionState()
    }

    override suspend fun getPersonProfile(): PersonProfile? {
        return getSelfProfile()
    }

    override suspend fun refreshPersonProfile() {
        _personProfileFlow.value = getSelfProfile() ?: PersonProfile.Empty
    }

    override suspend fun updateProfile(
        avatarUrl: String,
        nickname: String,
        signature: String
    ): ActionResult {
        val originProfile = getSelfProfileOrigin() ?: return ActionResult.Failed(
            reason = StringResources.getString(resId = R.string.error_update_failed)
        )
        originProfile.faceUrl = avatarUrl
        originProfile.setNickname(nickname)
        originProfile.selfSignature = signature
        return suspendTimCallback { callback ->
            V2TIMManager.getInstance().setSelfInfo(originProfile, callback)
        }
    }

}
