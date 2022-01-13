package github.leavesczy.compose_chat.proxy.logic

import android.content.Context
import android.util.Log
import com.tencent.imsdk.v2.*
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.base.model.ServerState
import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.proxy.consts.AppConst
import github.leavesczy.compose_chat.proxy.utils.GenerateUserSig
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Date: 2021/7/9 22:33
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AccountProvider : IAccountProvider, Converters {

    override val personProfile = MutableStateFlow(PersonProfile.Empty)

    override val serverConnectState = MutableSharedFlow<ServerState>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun init(context: Context) {
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_WARN
        V2TIMManager.getInstance().addIMSDKListener(object : V2TIMSDKListener() {

            override fun onConnecting() {
                log("onConnecting")
                dispatchServerState(serverState = ServerState.Connecting)
            }

            override fun onConnectSuccess() {
                log("onConnectSuccess")
                dispatchServerState(serverState = ServerState.ConnectSuccess)
            }

            override fun onConnectFailed(code: Int, error: String) {
                log("onConnectFailed")
                dispatchServerState(serverState = ServerState.ConnectFailed)
            }

            override fun onUserSigExpired() {
                log("onUserSigExpired")
                dispatchServerState(serverState = ServerState.UserSigExpired)
            }

            override fun onKickedOffline() {
                log("onKickedOffline")
                dispatchServerState(serverState = ServerState.KickedOffline)
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
                refreshPersonProfile()
            }
        })
        V2TIMManager.getInstance().initSDK(context, AppConst.APP_ID, config)
    }

    private fun dispatchServerState(serverState: ServerState) {
        coroutineScope.launch {
            serverConnectState.emit(value = serverState)
        }
    }

    override suspend fun login(userId: String): ActionResult {
        val formatUserId = userId.lowercase()
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance()
                .login(formatUserId, GenerateUserSig.genUserSig(formatUserId),
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
            V2TIMManager.getInstance().logout(object : V2TIMCallback {
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
            })
        }
    }

    override fun refreshPersonProfile() {
        coroutineScope.launch {
            getSelfProfileOrigin()?.let {
                convertPersonProfile(userFullInfo = it)
            }?.let {
                AppConst.personProfile.value = it
                personProfile.value = it
            }
        }
    }

    override suspend fun updatePersonProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ): Boolean {
        val originProfile = getSelfProfileOrigin() ?: return false
        return suspendCancellableCoroutine { continuation ->
            originProfile.faceUrl = faceUrl
            originProfile.setNickname(nickname)
            originProfile.selfSignature = signature
            V2TIMManager.getInstance().setSelfInfo(originProfile, object : V2TIMCallback {
                override fun onSuccess() {
                    continuation.resume(value = true)
                }

                override fun onError(code: Int, desc: String?) {
                    continuation.resume(value = false)
                }
            })
        }
    }

    private suspend fun getSelfProfileOrigin(): V2TIMUserFullInfo? {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance()
                .getUsersInfo(listOf(V2TIMManager.getInstance().loginUser), object :
                    V2TIMValueCallback<List<V2TIMUserFullInfo>> {
                    override fun onSuccess(t: List<V2TIMUserFullInfo>) {
                        continuation.resume(value = t[0])
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = null)
                    }
                })
        }
    }

    private fun log(log: String) {
        Log.e("AccountProvider", log)
    }

}