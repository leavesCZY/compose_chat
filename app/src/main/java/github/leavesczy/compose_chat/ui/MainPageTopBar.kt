package github.leavesczy.compose_chat.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.ui.logic.MainPageTopBarViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.ComposeDropdownMenuItem
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageTopBar(
    viewState: MainPageTopBarViewState,
    showFriendshipDialog: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    val coroutineScope = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        modifier = Modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF101010.color),
        title = {

        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 26.dp),
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null
                    )
                },
                onClick = {
                    coroutineScope.launch {
                        viewState.openDrawer()
                    }
                }
            )
        },
        actions = {
            Box(modifier = Modifier) {
                IconButton(
                    modifier = Modifier,
                    content = {
                        Icon(
                            modifier = Modifier
                                .size(size = 26.dp),
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = true
                    }
                )
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(end = 10.dp)
                ) {
                    DropdownMenu(
                        modifier = Modifier,
                        containerColor = ComposeChatTheme.colorScheme.c_FFEFF1F3_FF22202A.color,
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        ComposeDropdownMenuItem(
                            modifier = Modifier,
                            text = "添加好友",
                            onClick = {
                                menuExpanded = false
                                showFriendshipDialog()
                            }
                        )
                        ComposeDropdownMenuItem(
                            modifier = Modifier,
                            text = "加入群聊",
                            onClick = {
                                menuExpanded = false
                                showFriendshipDialog()
                            }
                        )
                    }
                }
            }
        }
    )
}