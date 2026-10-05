package github.leavesczy.compose_chat.di

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

object ComposeChat {

    val accountProvider: IAccountProvider = AccountProvider

    val conversationProvider: IConversationProvider = ConversationProvider

    val friendshipProvider: IFriendshipProvider = FriendshipProvider

    val messageProvider: IMessageProvider = MessageProvider

    val groupProvider: IGroupProvider = GroupProvider

    val demoSampleGroupIds = listOf(
        "@TGS#3SSMB3WHI",
        "@TGS#3VOZA3WHT",
        "@TGS#3W42A3WHP",
        "@TGS#3DMJIK6MS",
        "@TGS#3YCNIK6MC"
    )

}