package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.theme.AppThemeMode

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Stable
enum class MainPageTab {
    Conversation,
    Friendship,
    Person;
}

@Stable
data class MainPageTopBarViewState(
    val onClickOpenDrawer: suspend () -> Unit,
    val onClickShowFriendshipDialog: () -> Unit
)

@Stable
data class MainPageBottomBarViewState(
    val selectedTab: MainPageTab,
    val unreadMessageCount: Long,
    val onClickTab: (tab: MainPageTab) -> Unit
)

@Stable
data class MainPageDrawerViewState(
    val drawerState: DrawerState,
    val personProfile: PersonProfile,
    val appTheme: AppThemeMode,
    val onClickPreviewImage: (imageUrl: String) -> Unit,
    val onClickSwitchTheme: () -> Unit,
    val onClickUpdateProfile: () -> Unit,
    val onClickLogout: () -> Unit
)