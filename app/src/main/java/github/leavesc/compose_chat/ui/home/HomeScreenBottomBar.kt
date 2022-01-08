package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.model.HomeScreenBottomBarState
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.ui.weigets.CommonDivider

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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
    ) {
        CommonDivider()
        Row {
            for (screen in homeScreenBottomBarState.homeScreenList) {
                BottomNavigationItem(
                    modifier = Modifier.weight(1f),
                    screen = screen,
                    tabSelected = homeScreenBottomBarState.homeScreenSelected == screen,
                    unreadMessageCount = homeScreenBottomBarState.unreadMessageCount,
                    onClickTab = {
                        homeScreenBottomBarState.onHomeScreenTabSelected(screen)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier,
    screen: HomeScreenTab,
    tabSelected: Boolean,
    unreadMessageCount: Long,
    onClickTab: () -> Unit,
) {
    val selectedContentColor = MaterialTheme.colors.primary
    val unselectedContentColor = selectedContentColor.copy(alpha = 0.5f)
    val color = if (tabSelected) selectedContentColor else unselectedContentColor
    Box(
        modifier = modifier
            .height(height = 50.dp)
            .clickable {
                onClickTab()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 2.dp)
                .size(size = 28.dp)
                .align(alignment = Alignment.Center),
            tint = color,
            imageVector = screen.icon,
            contentDescription = null
        )
        if (unreadMessageCount > 0 && screen == HomeScreenTab.Conversation) {
            Text(
                text = if (unreadMessageCount > 99) "99+" else unreadMessageCount.toString(),
                color = Color.White,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = 18.dp, y = (-10).dp)
                    .size(size = 22.dp)
                    .background(color = MaterialTheme.colors.primary, shape = CircleShape)
                    .wrapContentSize(align = Alignment.Center)
            )
        }
    }
}