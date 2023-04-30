package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerState
import github.leavesczy.compose_chat.base.model.Conversation
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.PersonProfile

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
enum class MainTab {
    Conversation,
    Friendship,
    Person;
}

data class MainPageDrawerViewState(
    val drawerState: DrawerState,
    val personProfile: PersonProfile
)

data class MainPageBottomBarViewState(
    val selectedTab: MainTab,
    val unreadMessageCount: Long
)

data class ConversationPageViewState(
    val listState: LazyListState,
    val conversationList: List<Conversation>
)

data class FriendshipPageViewState(
    val listState: LazyListState,
    val joinedGroupList: List<GroupProfile>,
    val friendList: List<PersonProfile>
)

data class PersonProfilePageViewState(
    val personProfile: PersonProfile
)

data class FriendshipDialogViewState(
    val visible: Boolean,
    val onDismissRequest: () -> Unit,
    val joinGroup: (groupId: String) -> Unit,
    val addFriend: (userId: String) -> Unit
)

data class FriendProfilePageViewState(
    val personProfile: PersonProfile,
    val itIsMe: Boolean,
    val isFriend: Boolean
)

data class ProfileUpdatePageViewStata(
    val personProfile: PersonProfile
)

enum class AppTheme {

    Light,
    Dark,
    Gray;

    companion object {

        val Default = Light

    }

    fun nextTheme(): AppTheme {
        val values = values()
        return values.getOrElse(ordinal + 1) {
            values[0]
        }
    }

}