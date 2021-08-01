package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.base.provider.IFriendshipProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/6/27 15:12
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class FriendshipProvider : IFriendshipProvider, Converters {

    override val friendList = MutableStateFlow<List<PersonProfile>>(emptyList())

    init {
        V2TIMManager.getFriendshipManager().setFriendListener(object : V2TIMFriendshipListener() {
            override fun onFriendInfoChanged(infoList: MutableList<V2TIMFriendInfo>) {
                getFriendList()
            }

            override fun onFriendListAdded(users: MutableList<V2TIMFriendInfo>) {
                getFriendList()
            }

            override fun onFriendListDeleted(userList: MutableList<String>) {
                getFriendList()
                GlobalScope.launch {
                    userList.forEach {
                        deleteC2CConversation(it)
                    }
                }
            }
        })
    }

    override fun getFriendList() {
        GlobalScope.launch(Dispatchers.Main) {
            friendList.value = getFriendListOrigin() ?: emptyList()
        }
    }

    override suspend fun getFriendProfile(friendId: String): PersonProfile? {
        return getFriendInfo(friendId)?.let {
            convertFriend(it.friendInfo)
        }
    }

    private suspend fun getFriendListOrigin(): List<PersonProfile>? {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getFriendshipManager().getFriendList(object :
                    V2TIMValueCallback<List<V2TIMFriendInfo>> {
                    override fun onSuccess(result: List<V2TIMFriendInfo>) {
                        continuation.resume(convertFriend(result))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(null)
                    }
                })
            }
        }
    }

    private fun convertFriend(v2TIMFriendInfo: V2TIMFriendInfo): PersonProfile {
        return PersonProfile(
            userId = v2TIMFriendInfo.userID ?: "",
            nickname = v2TIMFriendInfo.userProfile?.nickName ?: "",
            remark = v2TIMFriendInfo.friendRemark ?: "",
            faceUrl = v2TIMFriendInfo.userProfile?.faceUrl ?: "",
            signature = v2TIMFriendInfo.userProfile?.selfSignature ?: ""
        )
    }

    private fun convertFriend(friendInfoList: List<V2TIMFriendInfo>): List<PersonProfile> {
        return friendInfoList.map { convertFriend(it) }.sortedBy { it.showName }
    }

    private suspend fun getFriendInfo(friendId: String): V2TIMFriendInfoResult? {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getFriendshipManager().getFriendsInfo(listOf(friendId), object :
                    V2TIMValueCallback<List<V2TIMFriendInfoResult>> {
                    override fun onSuccess(t: List<V2TIMFriendInfoResult>?) {
                        continuation.resume(t?.getOrNull(0))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(null)
                    }
                })
            }
        }
    }

    override suspend fun addFriend(friendId: String): ActionResult {
        val formatUserId = friendId.lowercase()
        if (formatUserId.isBlank()) {
            return ActionResult.Failed("请输入 UserID")
        }
        if (formatUserId == V2TIMManager.getInstance().loginUser ?: "") {
            return ActionResult.Failed("别玩啦~")
        }
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val requiresOpt = V2TIMFriendAddApplication(formatUserId)
                requiresOpt.setAddType(V2TIMFriendInfo.V2TIM_FRIEND_TYPE_BOTH)
                V2TIMManager.getFriendshipManager().addFriend(requiresOpt,
                    object : V2TIMValueCallback<V2TIMFriendOperationResult> {
                        override fun onSuccess(t: V2TIMFriendOperationResult) {
                            continuation.resume(ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(ActionResult.Failed(desc ?: ""))
                        }
                    }
                )
            }
        }
    }

    override suspend fun deleteFriend(friendId: String): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getFriendshipManager().deleteFromFriendList(
                    listOf(friendId), V2TIMFriendInfo.V2TIM_FRIEND_TYPE_BOTH,
                    object : V2TIMValueCallback<List<V2TIMFriendOperationResult>> {
                        override fun onSuccess(t: List<V2TIMFriendOperationResult>) {
                            continuation.resume(ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(ActionResult.Failed(desc ?: ""))
                        }
                    }
                )
            }
        }
    }

    override suspend fun setFriendRemark(friendId: String, remark: String): ActionResult {
        return withContext(Dispatchers.Main) {
            val friendProfile = getFriendInfo(friendId)?.friendInfo
                ?: return@withContext ActionResult.Failed(reason = "设置失败")
            friendProfile.friendRemark = remark
            suspendCancellableCoroutine<ActionResult> { continuation ->
                V2TIMManager.getFriendshipManager()
                    .setFriendInfo(friendProfile, object : V2TIMCallback {
                        override fun onSuccess() {
                            continuation.resume(ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(ActionResult.Failed(desc ?: ""))
                        }
                    }
                    )
            }
        }
    }

}