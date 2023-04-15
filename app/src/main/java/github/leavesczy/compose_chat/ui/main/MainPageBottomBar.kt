package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.main.logic.MainPageBottomBarViewState
import github.leavesczy.compose_chat.ui.main.logic.MainTab
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageBottomBar(
    mainViewModel: MainViewModel, viewState: MainPageBottomBarViewState
) {
    Row(
        modifier = Modifier
            .shadow(elevation = 18.dp)
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(height = 54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBarItem(tab = MainTab.Conversation,
            icon = Icons.Filled.Cabin,
            selectedTab = viewState.selectedTab,
            unreadMessageCount = viewState.unreadMessageCount,
            onClick = {
                mainViewModel.onTabSelected(tab = MainTab.Conversation)
            })
        NavigationBarItem(tab = MainTab.Friendship,
            icon = Icons.Filled.Sailing,
            selectedTab = viewState.selectedTab,
            unreadMessageCount = viewState.unreadMessageCount,
            onClick = {
                mainViewModel.onTabSelected(tab = MainTab.Friendship)
            })
        NavigationBarItem(tab = MainTab.Person,
            icon = Icons.Filled.ColorLens,
            selectedTab = viewState.selectedTab,
            unreadMessageCount = viewState.unreadMessageCount,
            onClick = {
                mainViewModel.onTabSelected(tab = MainTab.Person)
            })
    }
}

@Composable
private fun RowScope.NavigationBarItem(
    tab: MainTab,
    selectedTab: MainTab,
    icon: ImageVector,
    unreadMessageCount: Long,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                modifier = Modifier.size(size = 22.dp),
                imageVector = icon,
                contentDescription = null
            )
            if (tab == MainTab.Conversation && unreadMessageCount > 0) {
                Text(
                    modifier = Modifier
                        .offset(
                            x = 18.dp, y = (-10).dp
                        )
                        .size(
                            size = 22.dp
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary, shape = CircleShape
                        )
                        .wrapContentSize(align = Alignment.Center),
                    text = if (unreadMessageCount > 99) {
                        "99+"
                    } else {
                        unreadMessageCount.toString()
                    },
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }, selected = selectedTab == tab, colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary
        ), onClick = onClick
    )
}