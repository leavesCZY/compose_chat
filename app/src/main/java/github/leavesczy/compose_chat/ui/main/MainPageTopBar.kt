package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.model.MainPageAction
import github.leavesczy.compose_chat.model.MainTab

/**
 * @Author: leavesCZY
 * @Date: 2021/6/24 16:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageTopBar(tabSelected: MainTab, mainPageAction: MainPageAction) {
    if (tabSelected == MainTab.Person) {
        return
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    CenterAlignedTopAppBar(
        modifier = Modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        title = {

        },
        navigationIcon = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }, onClick = {
                mainPageAction.changDrawerState(DrawerValue.Open)
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
                Text(text = "添加好友", style = MaterialTheme.typography.bodyMedium)
            }, onClick = {
                menuExpanded = false
                mainPageAction.showFriendshipPanel()
            })
            DropdownMenuItem(text = {
                Text(text = "加入群聊", style = MaterialTheme.typography.bodyMedium)
            }, onClick = {
                menuExpanded = false
                mainPageAction.showFriendshipPanel()
            })
        }
    }
}