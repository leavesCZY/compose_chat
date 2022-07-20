package github.leavesczy.compose_chat.ui.main.logic

import github.leavesczy.compose_chat.common.provider.*
import github.leavesczy.compose_chat.proxy.logic.*

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 11:35
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object ComposeChat {

    const val groupId01 = "@TGS#3SSMB3WHI"

    const val groupId02 = "@TGS#3VOZA3WHT"

    const val groupId03 = "@TGS#3W42A3WHP"

    const val groupIdToUploadAvatar = "@TGS#aZRGY4WHQ"

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider()

    val messageProvider: IMessageProvider = MessageProvider()

    val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val groupProvider: IGroupProvider = GroupProvider()

}