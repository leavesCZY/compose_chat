package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.base.model.GroupMemberProfile
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfilePageAction
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.ui.widgets.CoilImage
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel
import github.leavesczy.compose_chat.utils.randomFaceUrl

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private val headerPicHeightDp = 500.dp

@Composable
fun GroupProfilePage(
    groupProfileViewModel: GroupProfileViewModel,
    action: GroupProfilePageAction
) {
    val viewState = groupProfileViewModel.groupProfilePageViewState
    val density = LocalDensity.current.density
    val headerMaxOffsetPx by remember {
        mutableStateOf(value = density * (headerPicHeightDp.value))
    }
    var topBarAlpha by remember {
        mutableStateOf(value = 0f)
    }
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = "") {
        snapshotFlow {
            listState.firstVisibleItemScrollOffset
        }.collect {
            topBarAlpha = if (listState.firstVisibleItemIndex == 0) {
                minOf(it / headerMaxOffsetPx, 1f)
            } else {
                1f
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                item(key = "header") {
                    val groupProfile = viewState.groupProfile
                    ProfilePanel(
                        title = groupProfile.name,
                        subtitle = groupProfile.introduction,
                        introduction = "GroupID: ${groupProfile.id}\nCreateTime: ${groupProfile.createTimeFormat}\nMemberCount: ${groupProfile.memberCount}",
                        avatarUrl = groupProfile.faceUrl,
                        content = {

                        }
                    )
                }
                items(
                    items = viewState.memberList,
                    key = {
                        it.detail.id
                    }, itemContent = {
                        GroupMemberItem(
                            groupMemberProfile = it,
                            groupProfilePageAction = action
                        )
                    })
            }
            GroupProfilePageTopBar(
                title = viewState.groupProfile.name,
                alpha = topBarAlpha,
                groupProfilePageAction = action
            )
        }
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    groupProfilePageAction: GroupProfilePageAction
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                groupProfilePageAction.onClickMember(groupMemberProfile)
            },
    ) {
        val (avatarRef, showNameRef, roleRef, dividerRef) = createRefs()
        val verticalChain =
            createVerticalChain(showNameRef, roleRef, chainStyle = ChainStyle.Packed)
        constrain(ref = verticalChain) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                }
                .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
                .size(size = 50.dp)
                .clip(shape = CircleShape),
            data = groupMemberProfile.detail.faceUrl
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRef) {
                    linkTo(
                        start = avatarRef.end,
                        end = parent.end,
                        startMargin = 12.dp,
                        endMargin = 12.dp
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 2.dp),
            text = groupMemberProfile.detail.showName + "（${
                groupMemberProfile.detail.id + if (groupMemberProfile.isOwner) {
                    " - 群主"
                } else {
                    ""
                }
            }）",
            fontSize = 17.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = roleRef) {
                    linkTo(
                        start = showNameRef.start,
                        end = parent.end,
                        endMargin = 12.dp
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(top = 2.dp),
            text = "joinTime: ${groupMemberProfile.joinTimeFormat}",
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Divider(
            modifier = Modifier
                .constrainAs(ref = dividerRef) {
                    linkTo(start = avatarRef.end, end = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                },
            thickness = 0.2.dp
        )
    }
}

@Composable
private fun GroupProfilePageTopBar(
    title: String,
    alpha: Float,
    groupProfilePageAction: GroupProfilePageAction
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    val background = MaterialTheme.colorScheme.background.copy(alpha = alpha)
    val textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = background
            )
            .statusBarsPadding()
            .height(height = 44.dp)
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = title,
            color = textColor,
            fontSize = 19.sp,
        )
        Icon(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .padding(end = 12.dp)
                .size(size = 28.dp)
                .clickable {
                    menuExpanded = true
                },
            imageVector = Icons.Default.MoreVert,
            contentDescription = null
        )
    }
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
                Text(
                    text = "修改头像",
                    fontSize = 17.sp
                )
            }, onClick = {
                menuExpanded = false
                groupProfilePageAction.setAvatar(randomFaceUrl())
            })
            DropdownMenuItem(text = {
                Text(
                    text = "退出群聊",
                    fontSize = 17.sp
                )
            }, onClick = {
                menuExpanded = false
                groupProfilePageAction.quitGroup()
            })
        }
    }
}