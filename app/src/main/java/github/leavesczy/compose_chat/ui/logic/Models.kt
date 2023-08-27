package github.leavesczy.compose_chat.ui.logic

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.model.PersonProfile

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
enum class MainPageTab {
    Conversation,
    Friendship,
    Person;
}

@Stable
data class MainPageDrawerViewState(
    val drawerState: DrawerState,
    val personProfile: PersonProfile,
    val previewImage: (String) -> Unit,
    val switchTheme: () -> Unit,
    val updateProfile: () -> Unit,
    val logout: () -> Unit
)

@Stable
data class MainPageTopBarViewState(
    val openDrawer: suspend () -> Unit,
    val showFriendshipDialog: () -> Unit
)

@Stable
data class MainPageBottomBarViewState(
    val selectedTab: MainPageTab,
    val unreadMessageCount: Long,
    val onClickTab: (MainPageTab) -> Unit
)

@Stable
enum class AppTheme {
    Light,
    Dark,
    Gray;
}