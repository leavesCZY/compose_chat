package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMFriendCheckResult
import com.tencent.imsdk.v2.V2TIMFriendInfo
import com.tencent.imsdk.v2.V2TIMFriendInfoResult
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo
import com.tencent.imsdk.v2.V2TIMGroupTipsElem
import com.tencent.imsdk.v2.V2TIMImageElem
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ConversationType
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageDetail
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.utils.StringResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
internal object Converters {

    suspend fun getSelfProfileOrigin(): V2TIMUserFullInfo? {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getInstance().getUsersInfo(
                    listOf(V2TIMManager.getInstance().loginUser),
                    object : V2TIMValueCallback<List<V2TIMUserFullInfo>> {
                        override fun onSuccess(t: List<V2TIMUserFullInfo>) {
                            continuation.resume(value = t[0])
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(value = null)
                        }
                    }
                )
            }
        }
    }

    suspend fun getSelfProfile(): PersonProfile? {
        return withContext(context = Dispatchers.Main.immediate) {
            val profile = getSelfProfileOrigin()
            if (profile == null) {
                null
            } else {
                PersonProfile(
                    id = profile.userID ?: "",
                    nickname = profile.nickName?.trim() ?: "",
                    remark = profile.nickName?.trim() ?: "",
                    avatarUrl = profile.faceUrl ?: "",
                    addTime = 0,
                    signature = profile.selfSignature?.trim() ?: "",
                    isFriend = false
                )
            }
        }
    }

    fun convertFriendProfile(friendInfo: V2TIMFriendInfo): PersonProfile {
        return PersonProfile(
            id = friendInfo.userID ?: "",
            nickname = friendInfo.userProfile.nickName?.trim() ?: "",
            remark = friendInfo.friendRemark?.trim() ?: "",
            avatarUrl = friendInfo.userProfile.faceUrl ?: "",
            signature = friendInfo.userProfile.selfSignature?.trim() ?: "",
            addTime = friendInfo.friendAddTime,
            isFriend = true
        )
    }

    fun convertFriendProfile(friendInfo: V2TIMFriendInfoResult): PersonProfile {
        return PersonProfile(
            id = friendInfo.friendInfo.userID ?: "",
            nickname = friendInfo.friendInfo.userProfile.nickName?.trim() ?: "",
            remark = friendInfo.friendInfo.friendRemark?.trim() ?: "",
            avatarUrl = friendInfo.friendInfo.userProfile.faceUrl ?: "",
            signature = friendInfo.friendInfo.userProfile.selfSignature?.trim() ?: "",
            addTime = friendInfo.friendInfo.friendAddTime,
            isFriend = friendInfo.relation == V2TIMFriendCheckResult.V2TIM_FRIEND_RELATION_TYPE_BOTH_WAY || friendInfo.relation == V2TIMFriendCheckResult.V2TIM_FRIEND_RELATION_TYPE_IN_MY_FRIEND_LIST
        )
    }

    fun convertGroupMember(memberFullInfo: V2TIMGroupMemberFullInfo): GroupMemberProfile {
        val detail = PersonProfile(
            id = memberFullInfo.userID ?: "",
            avatarUrl = memberFullInfo.faceUrl ?: "",
            nickname = memberFullInfo.nickName?.trim() ?: "",
            remark = memberFullInfo.friendRemark?.trim() ?: "",
            addTime = 0,
            signature = "",
            isFriend = false
        )
        return GroupMemberProfile(
            detail = detail,
            isOwner = memberFullInfo.role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER,
            joinTime = memberFullInfo.joinTime * 1000L
        )
    }

    fun convertMessage(messageList: List<V2TIMMessage>?): List<Message> {
        return messageList?.map { timMessage ->
            convertMessage(timMessage = timMessage)
        } ?: emptyList()
    }

    fun convertMessage(timMessage: V2TIMMessage): Message {
        val senderProfile = PersonProfile(
            id = timMessage.sender,
            avatarUrl = timMessage.faceUrl ?: "",
            nickname = timMessage.nickName?.trim() ?: "",
            remark = timMessage.friendRemark?.trim() ?: "",
            addTime = 0,
            signature = "",
            isFriend = false
        )
        val messageDetail = MessageDetail(
            msgId = timMessage.msgID ?: "",
            state = convertMessageState(state = timMessage.status),
            milliseconds = timMessage.timestamp * 1000L,
            sender = senderProfile,
            isOwnMessage = timMessage.isSelf
        )
        val message = when (timMessage.elemType) {
            V2TIMMessage.V2TIM_ELEM_TYPE_TEXT -> {
                TextMessage(
                    messageDetail = messageDetail,
                    text = timMessage.textElem?.text ?: ""
                )
            }

            V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE -> {
                val imageElem = timMessage.imageElem
                val imageList = imageElem?.imageList
                if (!imageList.isNullOrEmpty()) {
                    val imagePath = imageElem.path
                    val origin = imageList[0].toImageElement(imagePath = imagePath)
                    val large = imageList.getOrNull(index = 1).toImageElement(imagePath = imagePath)
                    val thumb = imageList.getOrNull(index = 2).toImageElement(imagePath = imagePath)
                    ImageMessage(
                        messageDetail = messageDetail,
                        original = origin!!,
                        large = large,
                        thumb = thumb
                    )
                } else {
                    null
                }
            }

            V2TIMMessage.V2TIM_ELEM_TYPE_GROUP_TIPS -> {
                convertGroupTipsMessage(timMessage = timMessage)
            }

            else -> {
                null
            }
        } ?: TextMessage(
            messageDetail = messageDetail,
            text = StringResources.getString(
                resId = R.string.message_unsupported_type,
                timMessage.elemType
            )
        )
        message.tag = timMessage
        return message
    }

    private fun convertGroupTipsMessage(timMessage: V2TIMMessage): Message? {
        val groupTipsElem = timMessage.groupTipsElem
        if (groupTipsElem != null) {
            fun V2TIMGroupMemberInfo.showName(): String {
                val friendRemark = this.friendRemark?.trim()
                val nickname = this.nickName?.trim()
                val userId = this.userID
                return if (friendRemark.isNullOrBlank()) {
                    if (nickname.isNullOrBlank()) {
                        userId ?: ""
                    } else {
                        nickname
                    }
                } else {
                    friendRemark
                }
            }

            val messageDetail = MessageDetail(
                msgId = timMessage.msgID ?: "",
                state = MessageState.Success,
                milliseconds = timMessage.timestamp * 1000L,
                sender = PersonProfile(
                    id = timMessage.sender,
                    avatarUrl = "",
                    nickname = "",
                    remark = "",
                    addTime = 0,
                    signature = "",
                    isFriend = false
                ),
                isOwnMessage = false
            )
            val memberList = groupTipsElem.memberList
            val opMember = groupTipsElem.opMember
            val memberNames = kotlin.run {
                var append = ""
                memberList?.forEachIndexed { index, info ->
                    append += if (index == memberList.size - 1) {
                        info.showName()
                    } else {
                        info.showName() + "、"
                    }
                }
                append
            } + " "
            val opMemberName = opMember.showName() + " "
            val tips: String
            when (groupTipsElem.type) {
                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_JOIN, V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_INVITE -> {
                    tips = StringResources.getString(resId = R.string.group_tip_join, memberNames)
                }

                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_QUIT -> {
                    tips = StringResources.getString(resId = R.string.group_tip_quit, memberNames)
                }

                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_KICKED -> {
                    tips = StringResources.getString(resId = R.string.group_tip_kicked, memberNames)
                }

                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_SET_ADMIN -> {
                    tips =
                        StringResources.getString(resId = R.string.group_tip_set_admin, memberNames)
                }

                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_CANCEL_ADMIN -> {
                    tips = StringResources.getString(
                        resId = R.string.group_tip_cancel_admin,
                        memberNames
                    )
                }

                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_GROUP_INFO_CHANGE -> {
                    tips = StringResources.getString(
                        resId = R.string.group_tip_group_info_change,
                        opMemberName
                    )
                }

                V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_MEMBER_INFO_CHANGE -> {
                    tips = StringResources.getString(
                        resId = R.string.group_tip_member_info_change,
                        opMemberName
                    )
                }

                else -> {
                    tips = StringResources.getString(
                        resId = R.string.message_unsupported_group_tip,
                        groupTipsElem.type
                    )
                }
            }
            return SystemMessage(
                messageDetail = messageDetail,
                tips = tips
            )
        }
        return null
    }

    private fun V2TIMImageElem.V2TIMImage?.toImageElement(imagePath: String?): ImageMessage.ImageElement? {
        if (this == null) {
            return null
        }
        val mUrl = url
        val imageUrl = if (mUrl.isNullOrBlank()) {
            imagePath
        } else {
            mUrl
        } ?: ""
        return ImageMessage.ImageElement(
            width = width,
            height = height,
            url = imageUrl
        )
    }

    private fun convertMessageState(state: Int): MessageState {
        return when (state) {
            V2TIMMessage.V2TIM_MSG_STATUS_SENDING -> {
                MessageState.Sending
            }

            V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC -> {
                MessageState.Success
            }

            V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL -> {
                MessageState.Failed(
                    reason = StringResources.getString(resId = R.string.message_send_failed_unknown)
                )
            }

            else -> {
                MessageState.Success
            }
        }
    }

    private suspend fun deleteConversation(key: String): ActionResult {
        return withContext(context = Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getConversationManager()
                    .deleteConversation(key, object : V2TIMCallback {
                        override fun onSuccess() {
                            continuation.resume(value = ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(
                                value = ActionResult.Failed(
                                    code = code,
                                    reason = desc
                                )
                            )
                        }
                    })
            }
        }
    }

    suspend fun deleteC2CConversation(userId: String): ActionResult {
        return deleteConversation(key = getC2CConversationKey(userId = userId))
    }

    suspend fun deleteGroupConversation(groupId: String): ActionResult {
        return deleteConversation(key = getGroupConversationKey(groupId = groupId))
    }

    fun getConversationKey(conversation: Conversation): String {
        return when (conversation.type) {
            ConversationType.C2C -> {
                getC2CConversationKey(userId = conversation.id)
            }

            ConversationType.Group -> {
                getGroupConversationKey(groupId = conversation.id)
            }
        }
    }

    fun getConversationKey(chat: Chat): String {
        return when (chat) {
            is Chat.C2C -> {
                getC2CConversationKey(userId = chat.id)
            }

            is Chat.Group -> {
                getGroupConversationKey(groupId = chat.id)
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