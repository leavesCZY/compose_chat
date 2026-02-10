package github.leavesczy.compose_chat.proxy

import android.app.Application
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.proxy.Converters.getSelfProfile
import github.leavesczy.compose_chat.proxy.Converters.getSelfProfileOrigin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AccountProvider : IAccountProvider {

    override val personProfile = MutableStateFlow(value = PersonProfile.Empty)

    override val serverConnectState = MutableSharedFlow<ServerConnectState>()

    override fun init(application: Application) {
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_WARN
        V2TIMManager.getInstance().addIMSDKListener(object : V2TIMSDKListener() {
            override fun onConnecting() {
                dispatchServerConnectState(serverConnectState = ServerConnectState.Connecting)
            }

            override fun onConnectSuccess() {
                dispatchServerConnectState(serverConnectState = ServerConnectState.Connected)
            }

            override fun onConnectFailed(code: Int, error: String) {
                dispatchServerConnectState(serverConnectState = ServerConnectState.ConnectFailed)
            }

            override fun onUserSigExpired() {
                dispatchServerConnectState(serverConnectState = ServerConnectState.UserSigExpired)
            }

            override fun onKickedOffline() {
                dispatchServerConnectState(serverConnectState = ServerConnectState.KickedOffline)
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
                refreshPersonProfile()
            }
        })
        V2TIMManager.getInstance().initSDK(application, GenerateUserSig.APP_ID, config)
    }

    private fun dispatchServerConnectState(serverConnectState: ServerConnectState) {
        AppCoroutineScope.launch {
            this@AccountProvider.serverConnectState.emit(value = serverConnectState)
        }
    }

    override suspend fun login(userId: String): ActionResult {
        val formatUserId = userId.lowercase()
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance().login(
                formatUserId,
                GenerateUserSig.genUserSig(userId = formatUserId),
                object : V2TIMCallback {
                    override fun onSuccess() {
                        dispatchServerConnectState(serverConnectState = ServerConnectState.Connected)
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc ?: ""
                            )
                        )
                    }
                })
        }
    }

    override suspend fun logout(): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance().logout(
                object : V2TIMCallback {
                    override fun onSuccess() {
                        dispatchServerConnectState(serverConnectState = ServerConnectState.Logout)
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc ?: ""
                            )
                        )
                    }
                }
            )
        }
    }

    override suspend fun getPersonProfile(): PersonProfile? {
        return getSelfProfile()
    }

    override fun refreshPersonProfile() {
        AppCoroutineScope.launch {
            personProfile.emit(value = getSelfProfile() ?: PersonProfile.Empty)
        }
    }

    override suspend fun updatePersonProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ): ActionResult {
        val originProfile = getSelfProfileOrigin() ?: return ActionResult.Failed("更新失败")
        return suspendCancellableCoroutine { continuation ->
            originProfile.faceUrl = faceUrl.replace("\\s", "")
            originProfile.setNickname(nickname.replace("\\s", ""))
            originProfile.selfSignature = signature.replace("\\s", "")
            V2TIMManager.getInstance().setSelfInfo(
                originProfile, object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = ActionResult.Failed("code: $code desc: $desc"))
                    }
                }
            )
        }
    }

}