package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.GroupMemberProfile
import github.leavesc.compose_chat.base.model.GroupProfile
import github.leavesc.compose_chat.base.provider.IGroupProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/7/12 0:10
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class GroupProvider : IGroupProvider, Converters {

    override val joinedGroupList = MutableStateFlow<List<GroupProfile>>(emptyList())

    init {
        V2TIMManager.getInstance().addGroupListener(object : V2TIMGroupListener() {
            override fun onMemberEnter(
                groupID: String,
                memberList: MutableList<V2TIMGroupMemberInfo>
            ) {
                getJoinedGroupList()
            }

            override fun onQuitFromGroup(groupID: String) {
                getJoinedGroupList()
            }
        })
    }

    override suspend fun joinGroup(groupId: String): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance().joinGroup(groupId, "", object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(ActionResult.Failed(code = code, reason = desc ?: ""))
                    }
                })
            }
        }
    }

    override suspend fun quitGroup(groupId: String): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance().quitGroup(groupId, object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(ActionResult.Failed(code = code, reason = desc ?: ""))
                    }
                })
            }
        }
    }

    override suspend fun getGroupInfo(groupId: String): GroupProfile? {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getGroupManager().getGroupsInfo(listOf(groupId), object :
                    V2TIMValueCallback<List<V2TIMGroupInfoResult>> {
                    override fun onSuccess(t: List<V2TIMGroupInfoResult>) {
                        continuation.resume(convertGroup(t[0].groupInfo))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(null)
                    }
                })
            }
        }
    }

    override fun getJoinedGroupList() {
        coroutineScope.launch(Dispatchers.Main) {
            joinedGroupList.value = getJoinedGroupListOrigin().sortedBy { it.name }
        }
    }

    private suspend fun getJoinedGroupListOrigin(): List<GroupProfile> {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getGroupManager().getJoinedGroupList(object :
                    V2TIMValueCallback<List<V2TIMGroupInfo>> {
                    override fun onSuccess(t: List<V2TIMGroupInfo>) {
                        continuation.resume(convertGroup(t))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(emptyList())
                    }
                })
            }
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
        return groupProfileList?.mapNotNull { convertGroup(it) } ?: emptyList()
    }

    override suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile> {
        return withContext(Dispatchers.Main) {
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
            memberList.sortedBy { it.joinTime }
        }
    }

    private suspend fun getGroupMemberList(
        groupId: String,
        nextStep: Long
    ): Pair<List<GroupMemberProfile>, Long> {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getGroupManager().getGroupMemberList(groupId,
                    V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL,
                    nextStep,
                    object : V2TIMValueCallback<V2TIMGroupMemberInfoResult> {
                        override fun onSuccess(t: V2TIMGroupMemberInfoResult) {
                            continuation.resume(
                                Pair(
                                    convertGroupMember(t.memberInfoList),
                                    t.nextSeq
                                )
                            )
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(Pair(emptyList(), -111))
                        }
                    })
            }
        }
    }

    private fun convertGroupMember(groupMemberList: List<V2TIMGroupMemberFullInfo>?): List<GroupMemberProfile> {
        return groupMemberList?.map { convertGroupMember(it) } ?: emptyList()
    }

}