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

    override suspend fun joinGroup(groupId: String): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance().joinGroup(groupId, "", object : V2TIMCallback {
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
            joinedGroupList.value = getJoinedGroupListOrigin()
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
            notification = group.notification ?: ""
        )
    }

    private fun convertGroup(groupProfileList: List<V2TIMGroupInfo>?): List<GroupProfile> {
        return groupProfileList?.mapNotNull { convertGroup(it) } ?: emptyList()
    }

    override suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile> {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getGroupManager().getGroupMemberList(groupId,
                    V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL,
                    0,
                    object : V2TIMValueCallback<V2TIMGroupMemberInfoResult> {
                        override fun onSuccess(t: V2TIMGroupMemberInfoResult?) {
                            continuation.resume(convertGroupMember(t?.memberInfoList))
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(emptyList())
                        }
                    })
            }
        }
    }

    private fun convertGroupMember(memberFullInfo: V2TIMGroupMemberFullInfo?): GroupMemberProfile? {
        memberFullInfo ?: return null
        return GroupMemberProfile(
            userId = memberFullInfo.userID ?: "",
            faceUrl = memberFullInfo.faceUrl ?: "",
            nickname = memberFullInfo.nickName ?: "",
            remark = memberFullInfo.friendRemark ?: "",
            signature = "",
            role = convertRole(memberFullInfo.role),
            joinTime = memberFullInfo.joinTime,
        )
    }

    private fun convertRole(roleType: Int): String {
        return when (roleType) {
            V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_MEMBER -> {
                "群成员"
            }
            V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_ADMIN -> {
                "群管理员"
            }
            V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER -> {
                "群主"
            }
            else -> {
                "unknown"
            }
        }
    }

    private fun convertGroupMember(groupMemberList: List<V2TIMGroupMemberFullInfo>?): List<GroupMemberProfile> {
        return groupMemberList?.mapNotNull { convertGroupMember(it) } ?: emptyList()
    }

}