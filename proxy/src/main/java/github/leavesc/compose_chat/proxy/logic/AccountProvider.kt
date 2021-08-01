package github.leavesc.compose_chat.proxy.logic

import android.content.Context
import android.util.Log
import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.base.model.ServerState
import github.leavesc.compose_chat.base.provider.IAccountProvider
import github.leavesc.compose_chat.proxy.consts.AppConst
import github.leavesc.compose_chat.proxy.utils.GenerateUserSig
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/7/9 22:33
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class AccountProvider : IAccountProvider, Converters {

    override val userProfile = MutableStateFlow(PersonProfile.Empty)

    override val serverConnectState = MutableSharedFlow<ServerState>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private fun dispatchServerState(serverState: ServerState) {
        GlobalScope.launch(Dispatchers.Main) {
            serverConnectState.emit(serverState)
        }
    }

    override fun init(context: Context) {
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_WARN
        V2TIMManager.getInstance()
            .initSDK(context, AppConst.APP_ID, config, object : V2TIMSDKListener() {

                override fun onConnecting() {
                    log("onConnecting")
                    dispatchServerState(ServerState.Connecting)
                }

                override fun onConnectSuccess() {
                    log("onConnectSuccess")
                    dispatchServerState(ServerState.ConnectSuccess)
                }

                override fun onConnectFailed(code: Int, error: String) {
                    log("onConnectFailed")
                    dispatchServerState(ServerState.ConnectFailed)
                }

                override fun onUserSigExpired() {
                    log("onUserSigExpired")
                    dispatchServerState(ServerState.UserSigExpired)
                }

                override fun onKickedOffline() {
                    log("onKickedOffline")
                    dispatchServerState(ServerState.KickedOffline)
                }

                override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
                    getUserProfile()
                }
            })
    }

    override suspend fun login(userId: String): ActionResult {
        return withContext(Dispatchers.Main) {
            val formatUserId = userId.lowercase()
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance()
                    .login(formatUserId, GenerateUserSig.genUserSig(formatUserId),
                        object : V2TIMCallback {
                            override fun onSuccess() {
                                continuation.resume(ActionResult.Success)
                            }

                            override fun onError(code: Int, desc: String?) {
                                continuation.resume(ActionResult.Failed(desc ?: ""))
                            }
                        })
            }
        }
    }

    override suspend fun logout(): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance().logout(object : V2TIMCallback {
                    override fun onSuccess() {
                        dispatchServerState(ServerState.Logout)
                        continuation.resume(ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(ActionResult.Failed(desc ?: ""))
                    }
                })
            }
        }
    }

    override fun getUserProfile() {
        GlobalScope.launch(Dispatchers.Main) {
            getSelfProfileOrigin()?.let {
                convertProfile(it)
            }?.let {
                AppConst.userProfile.value = it
                userProfile.value = it
            }
        }
    }

    private fun convertProfile(timUserFullInfo: V2TIMUserFullInfo): PersonProfile {
        return PersonProfile(
            userId = timUserFullInfo.userID ?: "",
            nickname = timUserFullInfo.nickName ?: "",
            remark = timUserFullInfo.nickName ?: "",
            faceUrl = timUserFullInfo.faceUrl ?: "",
            signature = timUserFullInfo.selfSignature ?: ""
        )
    }

    override suspend fun updateProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ): Boolean {
        return withContext(Dispatchers.Main) {
            val originProfile = getSelfProfileOrigin() ?: return@withContext false
            suspendCancellableCoroutine { continuation ->
                originProfile.faceUrl = faceUrl
                originProfile.setNickname(nickname)
                originProfile.selfSignature = signature
                V2TIMManager.getInstance().setSelfInfo(originProfile, object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(true)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(false)
                    }
                })
            }
        }
    }

    private suspend fun getSelfProfileOrigin(): V2TIMUserFullInfo? {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance()
                    .getUsersInfo(listOf(V2TIMManager.getInstance().loginUser), object :
                        V2TIMValueCallback<List<V2TIMUserFullInfo>> {
                        override fun onSuccess(t: List<V2TIMUserFullInfo>) {
                            continuation.resume(t[0])
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(null)
                        }
                    })
            }
        }
    }

    private fun log(log: String) {
        Log.e("AccountProvider", log)
    }

}