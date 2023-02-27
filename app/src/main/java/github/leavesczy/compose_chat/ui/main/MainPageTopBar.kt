package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageTopBar(mainViewModel: MainViewModel) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        title = {

        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null
                    )
                }, onClick = {
                    coroutineScope.launch {
                        mainViewModel.drawerViewState.drawerState.open()
                    }
                }
            )
        },
        actions = {
            Box(modifier = Modifier) {
                IconButton(content = {
                    Icon(
                        imageVector = Icons.Filled.MoreVert, contentDescription = null
                    )
                }, onClick = {
                    menuExpanded = true
                })
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(end = 10.dp)
                ) {
                    DropdownMenu(modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }) {
                        DropdownMenuItem(text = {
                            Text(
                                text = "添加好友",
                                fontSize = 17.sp
                            )
                        }, onClick = {
                            menuExpanded = false
                            mainViewModel.showFriendshipDialog()
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = "加入群聊",
                                fontSize = 17.sp
                            )
                        }, onClick = {
                            menuExpanded = false
                            mainViewModel.showFriendshipDialog()
                        })
                    }
                }
            }
        })
}