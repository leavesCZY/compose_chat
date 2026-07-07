package github.leavesczy.compose_chat.ui.main.logic

import androidx.annotation.CallSuper
import github.leavesczy.compose_chat.base.BaseViewModel
import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider

abstract class BaseMainViewModel : BaseViewModel() {

    protected val accountProvider: IAccountProvider = ComposeChat.accountProvider

    protected val conversationProvider: IConversationProvider = ComposeChat.conversationProvider

    protected val friendshipProvider: IFriendshipProvider = ComposeChat.friendshipProvider

    @CallSuper
    protected abstract fun showFriendshipDialog()

}