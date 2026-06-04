package github.leavesczy.compose_chat.ui.main.logic

import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.proxy.AccountProvider
import github.leavesczy.compose_chat.proxy.ConversationProvider
import github.leavesczy.compose_chat.proxy.FriendshipProvider
import github.leavesczy.compose_chat.proxy.GroupProvider
import github.leavesczy.compose_chat.proxy.MessageProvider

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
object ComposeChat {

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider

    val friendshipProvider: IFriendshipProvider = FriendshipProvider

    val messageProvider: IMessageProvider = MessageProvider

    val groupProvider: IGroupProvider = GroupProvider

}