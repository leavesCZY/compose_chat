package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMFriendAddApplication
import com.tencent.imsdk.v2.V2TIMFriendInfo
import com.tencent.imsdk.v2.V2TIMFriendInfoResult
import com.tencent.imsdk.v2.V2TIMFriendOperationResult
import com.tencent.imsdk.v2.V2TIMFriendshipListener
import com.tencent.imsdk.v2.V2TIMManager
import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.utils.StringResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FriendshipProvider : IFriendshipProvider {

    private val _friendListFlow = MutableStateFlow<List<PersonProfile>>(value = emptyList())

    override val friendListFlow: StateFlow<List<PersonProfile>> = _friendListFlow.asStateFlow()

    private val _friendListLoadErrorFlow = MutableStateFlow<String?>(value = null)

    override val friendListLoadErrorFlow: StateFlow<String?> =
        _friendListLoadErrorFlow.asStateFlow()

    private val _friendListLoadingFlow = MutableStateFlow(value = false)

    override val friendListLoadingFlow: StateFlow<Boolean> = _friendListLoadingFlow.asStateFlow()

    private var refreshJob: Job? = null

    @Volatile
    private var refreshGeneration = 0

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
                AppCoroutineScope.launch {
                    userList.forEach { userId ->
                        Converters.deleteC2CConversation(userId = userId)
                    }
                }
            }
        })
    }

    override fun refreshFriendList() {
        refreshJob?.cancel()
        val generation = ++refreshGeneration
        refreshJob = AppCoroutineScope.launch {
            _friendListLoadingFlow.value = _friendListFlow.value.isEmpty()
            try {
                when (val result = getFriendListOrigin()) {
                    is FriendListFetchResult.Success -> {
                        if (generation != refreshGeneration) {
                            return@launch
                        }
                        _friendListLoadErrorFlow.value = null
                        _friendListFlow.value = result.friendList
                    }

                    is FriendListFetchResult.Failed -> {
                        if (generation != refreshGeneration) {
                            return@launch
                        }
                        _friendListLoadErrorFlow.value = result.reason
                        if (_friendListFlow.value.isEmpty()) {
                            _friendListFlow.value = emptyList()
                        }
                    }
                }
            } finally {
                if (generation == refreshGeneration) {
                    _friendListLoadingFlow.value = false
                }
            }
        }
    }

    override fun clearSessionState() {
        refreshJob?.cancel()
        refreshGeneration++
        _friendListFlow.value = emptyList()
        _friendListLoadErrorFlow.value = null
        _friendListLoadingFlow.value = false
    }

    override suspend fun getFriendProfile(friendId: String): PersonProfile? {
        return getFriendInfo(friendId = friendId)?.let { friendInfoResult ->
            Converters.convertFriendProfile(friendInfo = friendInfoResult)
        }
    }

    private suspend fun getFriendListOrigin(): FriendListFetchResult {
        return withContext(context = Dispatchers.Main.immediate) {
            val friendInfoList = suspendTimValueOrNull<List<V2TIMFriendInfo>> { callback ->
                V2TIMManager.getFriendshipManager().getFriendList(callback)
            }
            if (friendInfoList == null) {
                FriendListFetchResult.Failed(
                    reason = StringResources.getString(resId = R.string.error_load_friend_list)
                )
            } else {
                FriendListFetchResult.Success(
                    friendList = convertFriend(
                        friendInfoList = friendInfoList.filter { friendInfo ->
                            !friendInfo.userID.isNullOrBlank()
                        }
                    ).sortedByDescending { friend ->
                        friend.addTime
                    }
                )
            }
        }
    }

    private fun convertFriend(friendInfoList: List<V2TIMFriendInfo>): List<PersonProfile> {
        return friendInfoList.map { friendInfo ->
            Converters.convertFriendProfile(friendInfo = friendInfo)
        }
    }

    private suspend fun getFriendInfo(friendId: String): V2TIMFriendInfoResult? {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendTimValueOrNull<List<V2TIMFriendInfoResult>> { callback ->
                V2TIMManager.getFriendshipManager().getFriendsInfo(
                    listOf(friendId),
                    callback
                )
            }?.firstOrNull()
        }
    }

    override suspend fun addFriend(friendId: String): ActionResult {
        val formatUserId = friendId.lowercase()
        if (formatUserId.isBlank()) {
            return ActionResult.Failed(
                reason = StringResources.getString(resId = R.string.login_user_id_required)
            )
        }
        if (formatUserId == (V2TIMManager.getInstance().loginUser ?: "")) {
            return ActionResult.Failed(
                reason = StringResources.getString(resId = R.string.error_cannot_add_self)
            )
        }
        return withContext(context = Dispatchers.Main.immediate) {
            val friendApplication = V2TIMFriendAddApplication(formatUserId)
            friendApplication.setAddType(V2TIMFriendInfo.V2TIM_FRIEND_TYPE_BOTH)
            val result = suspendTimValueOrNull<V2TIMFriendOperationResult> { callback ->
                V2TIMManager.getFriendshipManager().addFriend(friendApplication, callback)
            } ?: return@withContext ActionResult.Failed(reason = null)
            val resultCode = result.resultCode
            if (resultCode == 0) {
                ActionResult.Success
            } else {
                ActionResult.Failed(
                    code = resultCode,
                    reason = result.resultInfo
                )
            }
        }
    }

    override suspend fun deleteFriend(friendId: String): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendTimValueResult<List<V2TIMFriendOperationResult>> { callback ->
                V2TIMManager.getFriendshipManager().deleteFromFriendList(
                    listOf(friendId),
                    V2TIMFriendInfo.V2TIM_FRIEND_TYPE_BOTH,
                    callback
                )
            }
        }
    }

    override suspend fun setFriendRemark(friendId: String, remark: String): ActionResult {
        val friendProfile = getFriendInfo(friendId)?.friendInfo
            ?: return ActionResult.Failed(
                reason = StringResources.getString(resId = R.string.error_set_remark_failed)
            )
        return withContext(context = Dispatchers.Main.immediate) {
            friendProfile.friendRemark = remark
            suspendTimCallback { callback ->
                V2TIMManager.getFriendshipManager().setFriendInfo(friendProfile, callback)
            }
        }
    }

    private sealed class FriendListFetchResult {
        data class Success(val friendList: List<PersonProfile>) : FriendListFetchResult()
        data class Failed(val reason: String) : FriendListFetchResult()
    }

}
