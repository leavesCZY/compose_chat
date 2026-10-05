package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMGroupChangeInfo
import com.tencent.imsdk.v2.V2TIMGroupInfo
import com.tencent.imsdk.v2.V2TIMGroupInfoResult
import com.tencent.imsdk.v2.V2TIMGroupListener
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult
import com.tencent.imsdk.v2.V2TIMManager
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.base.utils.StringResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object GroupProvider : IGroupProvider {

    private val _joinedGroupListFlow = MutableStateFlow<List<GroupProfile>>(value = emptyList())

    override val joinedGroupListFlow: StateFlow<List<GroupProfile>> =
        _joinedGroupListFlow.asStateFlow()

    private val _joinedGroupListLoadErrorFlow = MutableStateFlow<String?>(value = null)

    override val joinedGroupListLoadErrorFlow: StateFlow<String?> =
        _joinedGroupListLoadErrorFlow.asStateFlow()

    private val _joinedGroupListLoadingFlow = MutableStateFlow(value = false)

    override val joinedGroupListLoadingFlow: StateFlow<Boolean> =
        _joinedGroupListLoadingFlow.asStateFlow()

    private var refreshJob: Job? = null

    @Volatile
    private var refreshGeneration = 0

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

            override fun onGroupDismissed(groupID: String?, opUser: V2TIMGroupMemberInfo?) {
                refreshJoinedGroupList()
                if (!groupID.isNullOrBlank()) {
                    AppCoroutineScope.launch {
                        Converters.deleteGroupConversation(groupId = groupID)
                    }
                }
            }

            override fun onGroupRecycled(groupID: String?, opUser: V2TIMGroupMemberInfo?) {
                refreshJoinedGroupList()
                if (!groupID.isNullOrBlank()) {
                    AppCoroutineScope.launch {
                        Converters.deleteGroupConversation(groupId = groupID)
                    }
                }
            }

            override fun onMemberKicked(
                groupID: String?,
                opUser: V2TIMGroupMemberInfo?,
                memberList: MutableList<V2TIMGroupMemberInfo>?
            ) {
                refreshJoinedGroupList()
                val selfId = V2TIMManager.getInstance().loginUser.orEmpty()
                val kickedSelf = memberList?.any { member ->
                    member.userID == selfId
                } == true
                if (kickedSelf && !groupID.isNullOrBlank()) {
                    AppCoroutineScope.launch {
                        Converters.deleteGroupConversation(groupId = groupID)
                    }
                }
            }

            override fun onMemberLeave(groupID: String?, member: V2TIMGroupMemberInfo?) {
                refreshJoinedGroupList()
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
        return withContext(context = Dispatchers.Main.immediate) {
            suspendTimCallback { callback ->
                V2TIMManager.getInstance().joinGroup(groupId, "", callback)
            }
        }
    }

    override suspend fun quitGroup(groupId: String): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendTimCallback { callback ->
                V2TIMManager.getInstance().quitGroup(groupId, callback)
            }
        }
    }

    override suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            val v2TIMGroupInfo = V2TIMGroupInfo()
            v2TIMGroupInfo.groupID = groupId
            v2TIMGroupInfo.faceUrl = avatarUrl
            suspendTimCallback { callback ->
                V2TIMManager.getGroupManager().setGroupInfo(v2TIMGroupInfo, callback)
            }
        }
    }

    override suspend fun getGroupInfo(groupId: String): GroupProfile? {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendTimValueOrNull<List<V2TIMGroupInfoResult>> { callback ->
                V2TIMManager.getGroupManager().getGroupsInfo(listOf(groupId), callback)
            }?.firstOrNull()?.let { result ->
                convertGroup(groupProfile = result.groupInfo)
            }
        }
    }

    override fun refreshJoinedGroupList() {
        refreshJob?.cancel()
        val generation = ++refreshGeneration
        refreshJob = AppCoroutineScope.launch {
            _joinedGroupListLoadingFlow.value = _joinedGroupListFlow.value.isEmpty()
            try {
                when (val result = getJoinedGroupListOrigin()) {
                    is GroupListFetchResult.Success -> {
                        if (generation != refreshGeneration) {
                            return@launch
                        }
                        _joinedGroupListLoadErrorFlow.value = null
                        _joinedGroupListFlow.value = result.groupList.sortedBy { group ->
                            group.name
                        }
                    }

                    is GroupListFetchResult.Failed -> {
                        if (generation != refreshGeneration) {
                            return@launch
                        }
                        _joinedGroupListLoadErrorFlow.value = result.reason
                        if (_joinedGroupListFlow.value.isEmpty()) {
                            _joinedGroupListFlow.value = emptyList()
                        }
                    }
                }
            } finally {
                if (generation == refreshGeneration) {
                    _joinedGroupListLoadingFlow.value = false
                }
            }
        }
    }

    override fun clearSessionState() {
        refreshJob?.cancel()
        refreshGeneration++
        _joinedGroupListFlow.value = emptyList()
        _joinedGroupListLoadErrorFlow.value = null
        _joinedGroupListLoadingFlow.value = false
    }

    private suspend fun getJoinedGroupListOrigin(): GroupListFetchResult {
        return withContext(context = Dispatchers.Main.immediate) {
            val infoList = suspendTimValueOrNull<List<V2TIMGroupInfo>> { callback ->
                V2TIMManager.getGroupManager().getJoinedGroupList(callback)
            }
            if (infoList == null) {
                GroupListFetchResult.Failed(
                    reason = StringResources.getString(
                        resId = github.leavesczy.compose_chat.base.R.string.error_load_group_list
                    )
                )
            } else {
                GroupListFetchResult.Success(
                    groupList = convertGroup(
                        groupProfileList = infoList.filter { groupInfo ->
                            !groupInfo.groupID.isNullOrBlank()
                        }
                    )
                )
            }
        }
    }

    private fun convertGroup(groupProfile: V2TIMGroupInfo?): GroupProfile? {
        val group = groupProfile ?: return null
        return GroupProfile(
            id = group.groupID ?: "",
            avatarUrl = group.faceUrl ?: "",
            name = group.groupName?.trim() ?: "",
            introduction = group.introduction?.trim() ?: "",
            createTime = group.createTime * 1000L
        )
    }

    private fun convertGroup(groupProfileList: List<V2TIMGroupInfo>?): List<GroupProfile> {
        return groupProfileList?.mapNotNull { groupInfo ->
            convertGroup(groupProfile = groupInfo)
        } ?: emptyList()
    }

    override suspend fun getGroupMemberList(groupId: String): List<GroupMemberProfile>? {
        return withContext(context = Dispatchers.Main.immediate) {
            var nextStep = 0L
            val memberList = mutableListOf<GroupMemberProfile>()
            while (true) {
                val page = getGroupMemberListPage(
                    groupId = groupId,
                    nextStep = nextStep
                ) ?: return@withContext null
                memberList.addAll(elements = page.first)
                nextStep = page.second
                if (nextStep <= 0) {
                    break
                }
            }
            memberList.sortedByDescending { member ->
                if (member.isOwner) {
                    Long.MAX_VALUE
                } else {
                    member.joinTime
                }
            }
        }
    }

    private suspend fun getGroupMemberListPage(
        groupId: String,
        nextStep: Long
    ): Pair<List<GroupMemberProfile>, Long>? {
        return withContext(context = Dispatchers.Main.immediate) {
            val result = suspendTimValueOrNull<V2TIMGroupMemberInfoResult> { callback ->
                V2TIMManager.getGroupManager().getGroupMemberList(
                    groupId,
                    V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL,
                    nextStep,
                    callback
                )
            } ?: return@withContext null
            Pair(
                first = convertGroupMember(
                    groupMemberList = result.memberInfoList.filter { memberInfo ->
                        memberInfo.userID.isNotBlank()
                    }
                ),
                second = result.nextSeq
            )
        }
    }

    private fun convertGroupMember(groupMemberList: List<V2TIMGroupMemberFullInfo>?): List<GroupMemberProfile> {
        return groupMemberList?.map { memberFullInfo ->
            Converters.convertGroupMember(memberFullInfo = memberFullInfo)
        } ?: emptyList()
    }

    private sealed class GroupListFetchResult {
        data class Success(val groupList: List<GroupProfile>) : GroupListFetchResult()
        data class Failed(val reason: String) : GroupListFetchResult()
    }

}