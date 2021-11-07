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
            .wrapContentHeight()
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
    Column(modifier = modifier.clickable {
        onClickTab()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 2.dp)
                    .size(size = 28.dp)
                    .align(alignment = Alignment.TopCenter),
                tint = color
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
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            text = screen.name,
            color = color,
            style = MaterialTheme.typography.subtitle2,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
        )
    }
}