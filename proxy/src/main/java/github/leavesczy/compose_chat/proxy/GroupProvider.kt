package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMGroupChangeInfo
import com.tencent.imsdk.v2.V2TIMGroupInfo
import com.tencent.imsdk.v2.V2TIMGroupInfoResult
import com.tencent.imsdk.v2.V2TIMGroupListener
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProvider : IGroupProvider {

    override val joinedGroupList = MutableSharedFlow<List<GroupProfile>>()

    init {
        V2TIMManager.getInstance().addGroupListener(object : V2TIMGroupListener() {
            override fun onMemberEnter(
                groupId: String,
                memberList: MutableList<V2TIMGroupMemberInfo>
            ) {
                refreshJoinedGroupList()
            }

            override fun onGroupCreated(groupId: String?) {
                refreshJoinedGroupList()
            }

            override fun onQuitFromGroup(groupId: String) {
                refreshJoinedGroupList()
                AppCoroutineScope.launch {
                    Converters.deleteGroupConversation(groupId = groupId)
                }
            }

            override fun onGroupInfoChanged(
                groupID: String?,
                changeInfos: MutableList<V2TIMGroupChangeInfo>?
            ) {
                refreshJoinedGroupList()
            }
        })
    }

    override suspend fun joinGroup(groupId: String): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance().joinGroup(groupId, "", object : V2TIMCallback {
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

    override suspend fun quitGroup(groupId: String): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance().quitGroup(groupId, object : V2TIMCallback {
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

    override suspend fun transferGroupOwner(groupId: String, newOwnerUserID: String): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getGroupManager().transferGroupOwner(
                groupId,
                newOwnerUserID,
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

    override suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            val v2TIMGroupInfo = V2TIMGroupInfo()
            v2TIMGroupInfo.groupID = groupId
            v2TIMGroupInfo.faceUrl = avatarUrl
            V2TIMManager.getGroupManager().setGroupInfo(v2TIMGroupInfo, object : V2TIMCallback {
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

    override suspend fun getGroupInfo(groupId: String): GroupProfile? {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getGroupManager().getGroupsInfo(
                listOf(groupId),
                object : V2TIMValueCallback<List<V2TIMGroupInfoResult>> {
                    override fun onSuccess(t: List<V2TIMGroupInfoResult>) {
                        continuation.resume(value = convertGroup(t[0].groupInfo))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = null)
                    }
                })
        }
    }

    override fun refreshJoinedGroupList() {
        AppCoroutineScope.launch {
            joinedGroupList.emit(value = getJoinedGroupListOrigin().sortedBy { it.name })
        }
    }

    private suspend fun getJoinedGroupListOrigin(): List<GroupProfile> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getGroupManager()
                .getJoinedGroupList(object : V2TIMValueCallback<List<V2TIMGroupInfo>> {
                    override fun onSuccess(infoList: List<V2TIMGroupInfo>) {
                        continuation.resume(
                            value = convertGroup(groupProfileList = infoList.filter {
                                !it.groupID.isNullOrBlank()
                            })
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = emptyList())
                    }
                })
        }
    }

    private fun convertGroup(groupProfile: V2TIMGroupInfo?): GroupProfile? {
        val group = groupProfile ?: return null
        return GroupProfile(
            id = group.groupID ?: "",
            faceUrl = group.faceUrl ?: "",
            name = group.groupName ?: "",
            introduction = group.introduction ?: "",
            createTime = group.createTime
        )
    }

    private fun convertGroup(groupProfileList: List<V2TIMGroupInfo>?): List<GroupProfile> {
        return groupProfileList?.mapNotNull { convertGroup(groupProfile = it) } ?: emptyList()
    }

    override suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile> {
        var nextStep = 0L
        val memberList = mutableListOf<GroupMemberProfile>()
        while (true) {
            val pair = getGroupMemberList(
                groupId = groupId, nextStep = nextStep
            )
            memberList.addAll(pair.first)
            nextStep = pair.second
            if (nextStep <= 0) {
                break
            }
        }
        val sorted = memberList.sortedByDescending {
            if (it.isOwner) {
                Long.MAX_VALUE
            } else {
                it.joinTime
            }
        }
        return sorted
    }

    private suspend fun getGroupMemberList(
        groupId: String,
        nextStep: Long
    ): Pair<List<GroupMemberProfile>, Long> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getGroupManager().getGroupMemberList(
                groupId,
                V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL,
                nextStep,
                object : V2TIMValueCallback<V2TIMGroupMemberInfoResult> {
                    override fun onSuccess(t: V2TIMGroupMemberInfoResult) {
                        continuation.resume(
                            value = Pair(
                                convertGroupMember(t.memberInfoList.filter { it.userID.isNotBlank() }),
                                t.nextSeq
                            )
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = Pair(emptyList(), -111))
                    }
                })
        }
    }

    private fun convertGroupMember(groupMemberList: List<V2TIMGroupMemberFullInfo>?): List<GroupMemberProfile> {
        return groupMemberList?.map {
            Converters.convertGroupMember(memberFullInfo = it)
        } ?: emptyList()
    }

}