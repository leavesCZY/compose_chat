package github.leavesczy.compose_chat.proxy.logic

import android.app.Application
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.ServerState
import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.proxy.consts.AppConstant
import github.leavesczy.compose_chat.proxy.coroutine.ChatCoroutineScope
import github.leavesczy.compose_chat.proxy.utils.Converters.getSelfProfile
import github.leavesczy.compose_chat.proxy.utils.Converters.getSelfProfileOrigin
import github.leavesczy.compose_chat.proxy.utils.GenerateUserSig
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

    override val serverConnectState = MutableSharedFlow<ServerState>()

    override fun init(application: Application) {
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_WARN
        V2TIMManager.getInstance().addIMSDKListener(object : V2TIMSDKListener() {

            override fun onConnecting() {
                dispatchServerState(serverState = ServerState.Connecting)
            }

            override fun onConnectSuccess() {
                dispatchServerState(serverState = ServerState.Connected)
            }

            override fun onConnectFailed(code: Int, error: String) {
                dispatchServerState(serverState = ServerState.ConnectFailed)
            }

            override fun onUserSigExpired() {
                dispatchServerState(serverState = ServerState.UserSigExpired)
            }

            override fun onKickedOffline() {
                dispatchServerState(serverState = ServerState.KickedOffline)
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
                refreshPersonProfile()
            }
        })
        V2TIMManager.getInstance().initSDK(application, AppConstant.APP_ID, config)
    }

    private fun dispatchServerState(serverState: ServerState) {
        ChatCoroutineScope.launch {
            serverConnectState.emit(value = serverState)
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
                        dispatchServerState(serverState = ServerState.Logout)
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
        ChatCoroutineScope.launch {
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