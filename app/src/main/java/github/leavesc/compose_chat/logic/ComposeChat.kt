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

    const val groupIdA = "@TGS#3SSMB3WHI"

    const val groupIdB = "@TGS#3VOZA3WHT"

    const val groupIdC = "@TGS#3W42A3WHP"

    const val groupToUploadAvatar = "@TGS#aZRGY4WHQ"

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider()

    val messageProvider: IMessageProvider = MessageProvider()

    val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val groupProvider: IGroupProvider = GroupProvider()

}