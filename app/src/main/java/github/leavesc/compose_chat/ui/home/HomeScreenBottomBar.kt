package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.model.HomeScreenBottomBarState
import github.leavesc.compose_chat.model.HomeScreenTab

/**
 * @Author: leavesC
 * @Date: 2021/6/26 19:37
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun HomeScreenBottomBar(
    homeScreenBottomBarState: HomeScreenBottomBarState
) {
    NavigationBar(modifier = Modifier.height(height = 60.dp)) {
        homeScreenBottomBarState.tabList.forEach { tab ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null
                    )
                    if (tab == HomeScreenTab.Conversation) {
                        val unreadMessageCount = homeScreenBottomBarState.unreadMessageCount
                        if (unreadMessageCount > 0) {
                            Text(
                                text = if (unreadMessageCount > 99) "99+" else unreadMessageCount.toString(),
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .offset(x = 18.dp, y = (-10).dp)
                                    .size(size = 24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }
                    }
                },
                selected = homeScreenBottomBarState.tabSelected == tab,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    homeScreenBottomBarState.onTabSelected(tab)
                }
            )
        }
    }
}