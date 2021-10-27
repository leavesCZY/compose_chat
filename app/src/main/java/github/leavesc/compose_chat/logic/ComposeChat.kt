package github.leavesc.compose_chat.logic

import github.leavesc.compose_chat.base.provider.*
import github.leavesc.compose_chat.proxy.logic.*

/**
 * @Author: leavesC
 * @Date: 2021/6/22 11:35
 * @Desc:
 * @Github：https://github.com/leavesC
 */
object ComposeChat {

    const val groupIdA = "@TGS#3WQWTPRHA"

    const val groupIdB = "@TGS#3Y6Y6MRHG"

    const val groupIdC = "@TGS#37VDVPRHS"

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider()

    val messageProvider: IMessageProvider = MessageProvider()

    val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val groupProvider: IGroupProvider = GroupProvider()

}