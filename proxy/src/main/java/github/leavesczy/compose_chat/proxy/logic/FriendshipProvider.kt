package github.leavesczy.compose_chat.proxy.logic

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMFriendAddApplication
import com.tencent.imsdk.v2.V2TIMFriendInfo
import com.tencent.imsdk.v2.V2TIMFriendInfoResult
import com.tencent.imsdk.v2.V2TIMFriendOperationResult
import com.tencent.imsdk.v2.V2TIMFriendshipListener
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.proxy.coroutine.ChatCoroutineScope
import github.leavesczy.compose_chat.proxy.utils.Converters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendshipProvider : IFriendshipProvider {

    override val friendList = MutableStateFlow<List<PersonProfile>>(value = emptyList())

    init {
        V2TIMManager.getFriendshipManager().addFriendListener(object : V2TIMFriendshipListener() {
            override fun onFriendInfoChanged(infoList: MutableList<V2TIMFriendInfo>) {
                refreshFriendList()
            }

            override fun onFriendListAdded(users: MutableList<V2TIMFriendInfo>) {
                refreshFriendList()
            }

            override fun onFriendListDeleted(userList: MutableList<String>) {
                refreshFriendList()
                ChatCoroutineScope.launch {
                    userList.forEach {
                        Converters.deleteC2CConversation(it)
                    }
                }
            }
        })
    }

    override fun refreshFriendList() {
        ChatCoroutineScope.launch {
            friendList.emit(value = getFriendListOrigin()?.sortedBy {
                it.showName.first().uppercaseChar()
            } ?: emptyList())
        }
    }

    override suspend fun getFriendProfile(friendId: String): PersonProfile? {
        return getFriendInfo(friendId = friendId)?.let {
            Converters.convertFriendProfile(it)
        }
    }

    private suspend fun getFriendListOrigin(): List<PersonProfile>? {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getFriendshipManager()
                .getFriendList(object : V2TIMValueCallback<List<V2TIMFriendInfo>> {
                    override fun onSuccess(result: List<V2TIMFriendInfo>) {
                        continuation.resume(
                            value = convertFriend(friendInfoList = result.filter { !it.userID.isNullOrBlank() })
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = null)
                    }
                })
        }
    }

    private fun convertFriend(friendInfoList: List<V2TIMFriendInfo>): List<PersonProfile> {
        return friendInfoList.map {
            Converters.convertFriendProfile(friendInfo = it)
        }
    }

    private suspend fun getFriendInfo(friendId: String): V2TIMFriendInfoResult? {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getFriendshipManager().getFriendsInfo(listOf(friendId),
                object : V2TIMValueCallback<List<V2TIMFriendInfoResult>> {
                    override fun onSuccess(t: List<V2TIMFriendInfoResult>?) {
                        continuation.resume(value = t?.getOrNull(0))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = null)
                    }
                }
            )
        }
    }

    override suspend fun addFriend(friendId: String): ActionResult {
        val formatUserId = friendId.lowercase()
        if (formatUserId.isBlank()) {
            return ActionResult.Failed(reason = "请输入 UserID")
        }
        if (formatUserId == (V2TIMManager.getInstance().loginUser ?: "")) {
            return ActionResult.Failed(reason = "别玩啦~")
        }
        return suspendCancellableCoroutine { continuation ->
            val requiresOpt = V2TIMFriendAddApplication(formatUserId)
            requiresOpt.setAddType(V2TIMFriendInfo.V2TIM_FRIEND_TYPE_BOTH)
            V2TIMManager.getFriendshipManager().addFriend(
                requiresOpt,
                object : V2TIMValueCallback<V2TIMFriendOperationResult> {
                    override fun onSuccess(t: V2TIMFriendOperationResult) {
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code, reason = desc ?: ""
                            )
                        )
                    }
                }
            )
        }
    }

    override suspend fun deleteFriend(friendId: String): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getFriendshipManager().deleteFromFriendList(listOf(friendId),
                V2TIMFriendInfo.V2TIM_FRIEND_TYPE_BOTH,
                object : V2TIMValueCallback<List<V2TIMFriendOperationResult>> {
                    override fun onSuccess(t: List<V2TIMFriendOperationResult>) {
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

    override suspend fun setFriendRemark(friendId: String, remark: String): ActionResult {
        val friendProfile = getFriendInfo(friendId)?.friendInfo
            ?: return ActionResult.Failed(reason = "设置失败")
        friendProfile.friendRemark = remark.replace("\\s", "")
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getFriendshipManager().setFriendInfo(
                friendProfile,
                object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(code = code, reason = desc ?: "")
                        )
                    }
                })
        }
    }

}