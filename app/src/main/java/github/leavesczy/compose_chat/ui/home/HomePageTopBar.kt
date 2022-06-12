package github.leavesczy.compose_chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.model.HomePageTab
import github.leavesczy.compose_chat.model.HomePageTopBarState

/**
 * @Author: leavesCZY
 * @Date: 2021/6/24 16:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun HomePageTopBar(homePageTopBarState: HomePageTopBarState) {
    if (homePageTopBarState.pageSelected == HomePageTab.Person) {
        return
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {

        },
        navigationIcon = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }, onClick = {
                homePageTopBarState.openDrawer()
            })
        },
        actions = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }, onClick = {
                menuExpanded = true
            })
        }
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.TopEnd)
            .padding(end = 20.dp)
    ) {
        DropdownMenu(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        ) {
            DropdownMenuItem(text = {
                Text(text = "添加好友")
            }, onClick = {
                menuExpanded = false
                homePageTopBarState.onAddFriend()
            })
            DropdownMenuItem(text = {
                Text(text = "加入群聊")
            }, onClick = {
                menuExpanded = false
                homePageTopBarState.onJoinGroup()
            })
        }
    }
}