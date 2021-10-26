package github.leavesc.compose_chat.logic

import github.leavesc.compose_chat.base.provider.*
import github.leavesc.compose_chat.proxy.logic.*

/**
 * @Author: leavesC
 * @Date: 2021/6/22 11:35
 * @Desc:
 * @Github：https://github.com/leavesC
 */
object Chat {

    const val groupId = "@TGS#3Y6Y6MRHG"

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider()

    val fiendMessageProvider: IMessageProvider = FriendMessageProvider()

    val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val groupProvider: IGroupProvider = GroupProvider()

    val groupMessageProvider: IMessageProvider = GroupMessageProvider()

}