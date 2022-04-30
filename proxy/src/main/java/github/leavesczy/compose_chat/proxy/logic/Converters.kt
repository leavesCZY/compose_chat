package github.leavesczy.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.proxy.coroutineScope.ChatCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 14:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal interface Converters {

    companion object {

        private val chatCoroutineScope: CoroutineScope = ChatCoroutineScope()

    }

    val coroutineScope: CoroutineScope
        get() = chatCoroutineScope

    suspend fun getSelfProfileOrigin(): V2TIMUserFullInfo? {
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

    suspend fun getSelfProfile(): PersonProfile? {
        val profile = getSelfProfileOrigin()
        return if (profile == null) {
            null
        } else {
            convertPersonProfile(profile)
        }
    }

    fun convertPersonProfile(userFullInfo: V2TIMUserFullInfo): PersonProfile {
        return PersonProfile(
            id = userFullInfo.userID ?: "",
            nickname = userFullInfo.nickName ?: "",
            remark = userFullInfo.nickName ?: "",
            faceUrl = userFullInfo.faceUrl ?: "",
            signature = userFullInfo.selfSignature ?: ""
        )
    }

    fun convertFriendProfile(friendInfo: V2TIMFriendInfo): PersonProfile {
        return PersonProfile(
            id = friendInfo.userID ?: "",
            nickname = friendInfo.userProfile.nickName ?: "",
            remark = friendInfo.friendRemark ?: "",
            faceUrl = friendInfo.userProfile.faceUrl ?: "",
            signature = friendInfo.userProfile.selfSignature ?: "",
            isFriend = true
        )
    }

    fun convertFriendProfile(friendInfo: V2TIMFriendInfoResult): PersonProfile {
        return PersonProfile(
            id = friendInfo.friendInfo.userID ?: "",
            nickname = friendInfo.friendInfo.userProfile.nickName ?: "",
            remark = friendInfo.friendInfo.friendRemark ?: "",
            faceUrl = friendInfo.friendInfo.userProfile.faceUrl ?: "",
            signature = friendInfo.friendInfo.userProfile.selfSignature ?: "",
            isFriend = friendInfo.relation == V2TIMFriendCheckResult.V2TIM_FRIEND_RELATION_TYPE_BOTH_WAY ||
                    friendInfo.relation == V2TIMFriendCheckResult.V2TIM_FRIEND_RELATION_TYPE_IN_MY_FRIEND_LIST
        )
    }

    fun convertGroupMember(
        memberFullInfo: V2TIMGroupMemberInfo
    ): GroupMemberProfile {
        val fullInfo = memberFullInfo as? V2TIMGroupMemberFullInfo
        val role = fullInfo?.role ?: -11111
        val detail = PersonProfile(
            id = memberFullInfo.userID ?: "",
            faceUrl = memberFullInfo.faceUrl ?: "",
            nickname = memberFullInfo.nickName ?: "",
            remark = memberFullInfo.friendRemark ?: "",
            signature = ""
        )
        return GroupMemberProfile(
            detail = detail,
            role = convertRole(role),
            isOwner = role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER,
            joinTime = (memberFullInfo as? V2TIMGroupMemberFullInfo)?.joinTime ?: 0,
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

    fun convertMessage(messageList: List<V2TIMMessage>?): List<Message> {
        return messageList?.map { convertMessage(it) } ?: emptyList()
    }

    fun convertMessage(timMessage: V2TIMMessage): Message {
        val senderProfile = PersonProfile(
            id = timMessage.sender,
            faceUrl = timMessage.faceUrl ?: "",
            nickname = timMessage.nickName ?: "",
            remark = timMessage.friendRemark ?: "",
            signature = ""
        )
        val messageDetail = MessageDetail(
            msgId = timMessage.msgID ?: "",
            state = convertMessageState(timMessage.status),
            timestamp = timMessage.timestamp,
            sender = senderProfile,
            isSelfMessage = timMessage.isSelf
        )
        val message = when (val elemType = timMessage.elemType) {
            V2TIMMessage.V2TIM_ELEM_TYPE_TEXT -> {
                TextMessage(
                    detail = messageDetail,
                    text = timMessage.textElem?.text ?: ""
                )
            }
            V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE -> {
                val imageList = timMessage.imageElem?.imageList
                if (imageList.isNullOrEmpty()) {
                    TextMessage(
                        detail = messageDetail,
                        text = "[不支持的消息类型] - $elemType"
                    )
                } else {
                    val origin = imageList[0].toImageElement()
                    val large = imageList.getOrNull(1).toImageElement()
                    val thumb = imageList.getOrNull(2).toImageElement()
                    ImageMessage(
                        detail = messageDetail,
                        original = origin!!,
                        large = large,
                        thumb = thumb
                    )
                }
            }
            else -> {
                TextMessage(
                    detail = messageDetail,
                    text = "[不支持的消息类型] - $elemType"
                )
            }
        }
        message.tag = timMessage
        return message
    }

    private fun V2TIMImageElem.V2TIMImage?.toImageElement(): ImageElement? {
        if (this == null) {
            return null
        }
        return ImageElement(width = width, height = height, url = url)
    }

    private fun convertMessageState(state: Int): MessageState {
        return when (state) {
            V2TIMMessage.V2TIM_MSG_STATUS_SENDING -> {
                MessageState.Sending
            }
            V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC -> {
                MessageState.Completed
            }
            V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL -> {
                MessageState.SendFailed("unknown")
            }
            else -> {
                MessageState.Completed
            }
        }
    }

    suspend fun deleteConversation(key: String): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getConversationManager().deleteConversation(
                key, object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(ActionResult.Failed(desc ?: ""))
                    }
                })
        }
    }

    suspend fun deleteC2CConversation(userId: String): ActionResult {
        return deleteConversation(key = getC2CConversationKey(userId = userId))
    }

    suspend fun deleteGroupConversation(groupId: String): ActionResult {
        return deleteConversation(key = getGroupConversationKey(groupId = groupId))
    }

    fun getConversationKey(conversation: Conversation): String {
        return when (conversation) {
            is C2CConversation -> {
                getC2CConversationKey(userId = conversation.id)
            }
            is GroupConversation -> {
                getGroupConversationKey(groupId = conversation.id)
            }
        }
    }

    private fun getC2CConversationKey(userId: String): String {
        return String.format("c2c_%s", userId)
    }

    private fun getGroupConversationKey(groupId: String): String {
        return String.format("group_%s", groupId)
    }

}