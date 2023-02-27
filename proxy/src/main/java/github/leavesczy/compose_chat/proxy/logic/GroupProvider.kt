package github.leavesczy.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.GroupMemberProfile
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.proxy.coroutine.ChatCoroutineScope
import github.leavesczy.compose_chat.proxy.utils.Converters
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
                ChatCoroutineScope.launch {
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
            V2TIMManager.getGroupManager()
                .transferGroupOwner(groupId, newOwnerUserID, object : V2TIMCallback {
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
            V2TIMManager.getGroupManager().getGroupsInfo(listOf(groupId), object :
                V2TIMValueCallback<List<V2TIMGroupInfoResult>> {
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
        ChatCoroutineScope.launch {
            joinedGroupList.emit(value = getJoinedGroupListOrigin().sortedBy { it.name })
        }
    }

    private suspend fun getJoinedGroupListOrigin(): List<GroupProfile> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getGroupManager()
                .getJoinedGroupList(object : V2TIMValueCallback<List<V2TIMGroupInfo>> {
                    override fun onSuccess(infoList: List<V2TIMGroupInfo>) {
                        continuation.resume(value = convertGroup(groupProfileList = infoList.filter {
                            !it.groupID.isNullOrBlank()
                        }))
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
            createTime = group.createTime,
            memberCount = group.memberCount
        )
    }

    private fun convertGroup(groupProfileList: List<V2TIMGroupInfo>?): List<GroupProfile> {
        return groupProfileList?.mapNotNull { convertGroup(groupProfile = it) } ?: emptyList()
    }

    override suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile> {
        var nextStep = 0L
        val memberList = mutableListOf<GroupMemberProfile>()
        while (true) {
            val pair = getGroupMemberList(groupId = groupId, nextStep = nextStep)
            memberList.addAll(pair.first)
            nextStep = pair.second
            if (nextStep <= 0) {
                break
            }
        }
        memberList.sortBy { it.joinTime }
        val owner = memberList.find { it.isOwner }
        if (owner != null) {
            memberList.remove(owner)
            memberList.add(0, owner)
        }
        return memberList
    }

    private suspend fun getGroupMemberList(
        groupId: String,
        nextStep: Long
    ): Pair<List<GroupMemberProfile>, Long> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getGroupManager().getGroupMemberList(groupId,
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