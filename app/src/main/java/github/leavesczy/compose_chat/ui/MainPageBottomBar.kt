package github.leavesczy.compose_chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.logic.MainPageBottomBarViewState
import github.leavesczy.compose_chat.ui.logic.MainPageTab
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageBottomBar(viewState: MainPageBottomBarViewState) {
    Row(
        modifier = Modifier
            .shadow(elevation = 28.dp)
            .background(color = ComposeChatTheme.colorScheme.c_FFEFF1F3_FF22202A.color)
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(height = 54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (tab in MainPageTab.entries) {
            val selected = viewState.selectedTab == tab
            val icon: ImageVector
            val unreadMessageCount: Long
            when (tab) {
                MainPageTab.Conversation -> {
                    icon = Icons.Rounded.WbSunny
                    unreadMessageCount = viewState.unreadMessageCount
                }

                MainPageTab.Friendship -> {
                    icon = Icons.Filled.Sailing
                    unreadMessageCount = 0
                }

                MainPageTab.Person -> {
                    icon = Icons.Rounded.ColorLens
                    unreadMessageCount = 0
                }
            }
            NavigationBarItem(
                icon = icon,
                selected = selected,
                unreadMessageCount = unreadMessageCount,
                onClick = {
                    viewState.onClickTab(tab)
                }
            )
        }
    }
}

@Composable
private fun RowScope.NavigationBarItem(
    icon: ImageVector,
    selected: Boolean,
    unreadMessageCount: Long,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                modifier = Modifier
                    .size(size = 22.dp),
                imageVector = icon,
                contentDescription = null
            )
            if (unreadMessageCount > 0) {
                Text(
                    modifier = Modifier
                        .offset(x = 18.dp, y = (-10).dp)
                        .size(size = 22.dp)
                        .background(
                            color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
                            shape = CircleShape
                        )
                        .wrapContentSize(align = Alignment.Center),
                    text = if (unreadMessageCount > 99) {
                        "99+"
                    } else {
                        unreadMessageCount.toString()
                    },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center,
                    color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
                )
            }
        },
        selected = selected,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
            unselectedIconColor = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
        ),
        onClick = onClick
    )
}