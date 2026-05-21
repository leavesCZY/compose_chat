package github.leavesczy.compose_chat.proxy

import android.app.Application
import com.tencent.imsdk.v2.V2TIMCallback
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
import github.leavesczy.compose_chat.proxy.Converters.getSelfProfile
import github.leavesczy.compose_chat.proxy.Converters.getSelfProfileOrigin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class AccountProvider : IAccountProvider {

    override val personProfileFlow = MutableStateFlow(value = PersonProfile.Empty)

    override val serverConnectStateFlow = MutableSharedFlow<ServerConnectState>()

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
                dispatchServerConnectState(connectState = ServerConnectState.UserSigExpired)
            }

            override fun onKickedOffline() {
                dispatchServerConnectState(connectState = ServerConnectState.KickedOffline)
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
                updatePersonProfile(userFullInfo = info)
            }
        })
        V2TIMManager.getInstance().initSDK(application, GenerateUserSig.APP_ID, config)
    }

    private fun dispatchServerConnectState(connectState: ServerConnectState) {
        AppCoroutineScope.launch {
            serverConnectStateFlow.emit(value = connectState)
        }
    }

    override suspend fun login(userId: String): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                val formatUserId = userId.lowercase()
                V2TIMManager.getInstance().login(
                    formatUserId,
                    GenerateUserSig.genUserSig(userId = formatUserId),
                    object : V2TIMCallback {
                        override fun onSuccess() {
                            dispatchServerConnectState(connectState = ServerConnectState.Connected)
                            continuation.resume(value = ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(
                                value = ActionResult.Failed(
                                    code = code,
                                    reason = desc
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    override suspend fun logout(): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance().logout(
                    object : V2TIMCallback {
                        override fun onSuccess() {
                            dispatchServerConnectState(connectState = ServerConnectState.Logout)
                            continuation.resume(value = ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(
                                value = ActionResult.Failed(
                                    code = code,
                                    reason = desc
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    override suspend fun getPersonProfile(): PersonProfile? {
        return getSelfProfile()
    }

    private fun updatePersonProfile(userFullInfo: V2TIMUserFullInfo) {
        AppCoroutineScope.launch {
            val profile = PersonProfile(
                id = userFullInfo.userID ?: "",
                nickname = userFullInfo.nickName?.trim() ?: "",
                remark = userFullInfo.nickName?.trim() ?: "",
                avatarUrl = userFullInfo.faceUrl ?: "",
                addTime = 0,
                signature = userFullInfo.selfSignature?.trim() ?: "",
                isFriend = false
            )
            personProfileFlow.emit(value = profile)
        }
    }

    override suspend fun refreshPersonProfile() {
        personProfileFlow.emit(value = getSelfProfile() ?: PersonProfile.Empty)
    }

    override suspend fun updateProfile(
        avatarUrl: String,
        nickname: String,
        signature: String
    ): ActionResult {
        val originProfile = getSelfProfileOrigin() ?: return ActionResult.Failed(
            reason = StringResources.getString(resId = R.string.error_update_failed)
        )
        return suspendCancellableCoroutine { continuation ->
            originProfile.faceUrl = avatarUrl
            originProfile.setNickname(nickname)
            originProfile.selfSignature = signature
            V2TIMManager.getInstance().setSelfInfo(
                originProfile, object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc
                            )
                        )
                    }
                }
            )
        }
    }

}