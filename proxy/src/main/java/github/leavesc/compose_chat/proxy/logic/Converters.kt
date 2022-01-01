package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.proxy.coroutineScope.ChatCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/6/22 14:44
 * @Desc:
 * @Github：https://github.com/leavesC
 */
internal interface Converters {

    companion object {

        private val chatCoroutineScope: CoroutineScope = ChatCoroutineScope()

        fun convertPersonProfile(userFullInfo: V2TIMUserFullInfo): PersonProfile {
            return PersonProfile(
                userId = userFullInfo.userID ?: "",
                nickname = userFullInfo.nickName ?: "",
                remark = userFullInfo.nickName ?: "",
                faceUrl = userFullInfo.faceUrl ?: "",
                signature = userFullInfo.selfSignature ?: ""
            )
        }

        fun convertFriendProfile(friendInfo: V2TIMFriendInfo): PersonProfile {
            return convertPersonProfile(userFullInfo = friendInfo.userProfile)
        }

        fun convertFriendProfile(v2TIMFriendInfo: V2TIMFriendInfoResult): PersonProfile {
            return convertPersonProfile(userFullInfo = v2TIMFriendInfo.friendInfo.userProfile).copy(
                isFriend = v2TIMFriendInfo.relation == V2TIMFriendCheckResult.V2TIM_FRIEND_RELATION_TYPE_BOTH_WAY ||
                        v2TIMFriendInfo.relation == V2TIMFriendCheckResult.V2TIM_FRIEND_RELATION_TYPE_IN_MY_FRIEND_LIST
            )
        }

        fun convertGroupMember(
            memberFullInfo: V2TIMGroupMemberInfo
        ): GroupMemberProfile {
            val fullInfo = memberFullInfo as? V2TIMGroupMemberFullInfo
            val role = fullInfo?.role ?: -11111
            val detail = PersonProfile(
                userId = memberFullInfo.userID ?: "",
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

        suspend fun deleteConversation(id: String): ActionResult {
            return suspendCancellableCoroutine { continuation ->
                V2TIMManager.getConversationManager().deleteConversation(
                    id, object : V2TIMCallback {
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
            return deleteConversation(String.format("c2c_%s", userId))
        }

        suspend fun deleteGroupConversation(groupId: String): ActionResult {
            return deleteConversation(String.format("group_%s", groupId))
        }

        fun getConversationId(conversation: Conversation): String {
            return when (conversation) {
                is Conversation.C2CConversation -> {
                    String.format("c2c_%s", conversation.id)
                }
                is Conversation.GroupConversation -> {
                    String.format("group_%s", conversation.id)
                }
            }
        }

        fun convertMessage(messageList: List<V2TIMMessage>?): List<Message> {
            return messageList?.mapNotNull { convertMessage(it) } ?: emptyList()
        }

        fun convertMessage(timMessage: V2TIMMessage?, withGroupTip: Boolean = false): Message? {
            val groupId = timMessage?.groupID
            val userId = timMessage?.userID
            if (groupId.isNullOrBlank() && userId.isNullOrBlank()) {
                return null
            }
            val senderProfile = PersonProfile(
                userId = timMessage.sender,
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
            val message = when (timMessage.elemType) {
                V2TIMMessage.V2TIM_ELEM_TYPE_TEXT -> {
                    TextMessage(
                        detail = messageDetail,
                        msg = timMessage.textElem?.text ?: ""
                    )
                }
                V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE -> {
                    ImageMessage(
                        detail = messageDetail,
                        imagePath = timMessage.imageElem.path ?: ""
                    )
                }
                else -> {
                    TextMessage(
                        detail = messageDetail,
                        msg = "[未知消息的类型]"
                    )
                }
            }
            message.messageDetail.tag = timMessage
            return message
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
                    MessageState.SendFailed
                }
                else -> {
                    MessageState.Completed
                }
            }
        }

    }

    val coroutineScope: CoroutineScope
        get() = chatCoroutineScope

}