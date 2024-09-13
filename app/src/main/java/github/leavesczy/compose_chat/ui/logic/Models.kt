package github.leavesczy.compose_chat.ui.logic

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

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
    val personProfile: MutableState<PersonProfile>,
    val appTheme: MutableState<AppTheme>,
    val previewImage: (String) -> Unit,
    val switchTheme: () -> Unit,
    val updateProfile: () -> Unit,
    val logout: () -> Unit
)

@Stable
data class MainPageTopBarViewState(
    val openDrawer: suspend () -> Unit
)

@Stable
data class MainPageBottomBarViewState(
    val selectedTab: MutableState<MainPageTab>,
    val unreadMessageCount: MutableState<Long>,
    val onClickTab: (MainPageTab) -> Unit
)

@Stable
enum class AppTheme {
    Light,
    Dark,
    Gray;
}