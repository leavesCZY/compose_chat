package github.leavesczy.compose_chat.ui.main

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.main.logic.MainPageTopBarViewState
import github.leavesczy.compose_chat.widgets.ComposeDropdownMenuItem
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Composable
fun MainPageTopBar(viewState: MainPageTopBarViewState) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    val coroutineScope = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        modifier = Modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF101010.color),
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
                        viewState.onClickOpenDrawer()
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
                        containerColor = AppTheme.colorScheme.c_FFEFF1F3_FF22202A.color,
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        ComposeDropdownMenuItem(
                            modifier = Modifier,
                            text = stringResource(id = R.string.add_friend),
                            onClick = {
                                menuExpanded = false
                                viewState.onClickShowFriendshipDialog()
                            }
                        )
                        ComposeDropdownMenuItem(
                            modifier = Modifier,
                            text = stringResource(id = R.string.join_group_chat),
                            onClick = {
                                menuExpanded = false
                                viewState.onClickShowFriendshipDialog()
                            }
                        )
                    }
                }
            }
        }
    )
}