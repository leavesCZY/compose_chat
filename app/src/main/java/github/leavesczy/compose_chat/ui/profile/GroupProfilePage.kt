package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.GroupMemberProfile
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToHomePage
import github.leavesczy.compose_chat.extend.viewModelInstance
import github.leavesczy.compose_chat.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.model.Page
import github.leavesczy.compose_chat.ui.profile.ProfilePanel
import github.leavesczy.compose_chat.ui.widgets.CircleImage
import github.leavesczy.compose_chat.ui.widgets.CommonDivider
import github.leavesczy.compose_chat.utils.randomFaceUrl
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/10/27 18:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun GroupProfilePage(groupId: String) {
    val groupProfileViewModel = viewModelInstance {
        GroupProfileViewModel(groupId = groupId)
    }
    val groupProfilePageState by groupProfileViewModel.groupProfilePageState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val navHostController = LocalNavHostController.current
    Scaffold {
        Box {
            val onClickMember: (GroupMemberProfile) -> Unit = remember {
                object : (GroupMemberProfile) -> Unit {
                    override fun invoke(member: GroupMemberProfile) {
                        navHostController.navigate(
                            route = Page.FriendProfilePage.generateRoute(friendId = member.detail.id)
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item(key = true) {
                    ProfilePanel(groupProfile = groupProfilePageState.groupProfile)
                }
                val memberList = groupProfilePageState.memberList
                memberList.forEach {
                    item(key = it.detail.id) {
                        GroupMemberItem(groupMemberProfile = it, onClickMember = onClickMember)
                    }
                }
            }
            GroupProfilePageTopBar(
                randomAvatar = {
                    groupProfileViewModel.setAvatar(avatarUrl = randomFaceUrl())
                },
                quitGroup = {
                    coroutineScope.launch {
                        when (val result = groupProfileViewModel.quitGroup()) {
                            ActionResult.Success -> {
                                showToast("已退出群聊")
                                navHostController.navToHomePage()
                            }
                            is ActionResult.Failed -> {
                                showToast(result.reason)
                            }
                        }
                    }
                })
        }
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    onClickMember: (GroupMemberProfile) -> Unit
) {
    val padding = 10.dp
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClickMember(groupMemberProfile)
            },
    ) {
        val (avatar, showName, role, divider) = createRefs()
        CircleImage(
            data = groupMemberProfile.detail.faceUrl,
            modifier = Modifier
                .padding(start = padding, top = padding, bottom = padding)
                .size(size = 50.dp)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        Text(
            text = groupMemberProfile.detail.showName + "（${
                groupMemberProfile.detail.id + if (groupMemberProfile.isOwner) {
                    " - 群主"
                } else {
                    ""
                }
            }）",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = showName) {
                    start.linkTo(anchor = avatar.end)
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                    bottom.linkTo(anchor = role.top)
                    width = Dimension.fillToConstraints
                }
        )
        Text(
            text = "joinTime: ${groupMemberProfile.joinTimeFormat}",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = role) {
                    start.linkTo(anchor = showName.start)
                    top.linkTo(anchor = showName.bottom)
                    end.linkTo(anchor = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                }
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = divider) {
                    start.linkTo(anchor = avatar.end, margin = padding)
                    end.linkTo(anchor = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
private fun GroupProfilePageTopBar(
    randomAvatar: () -> Unit,
    quitGroup: () -> Unit,
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    SmallTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        title = {

        },
        actions = {
            Icon(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(size = 28.dp)
                    .clickable {
                        menuExpanded = true
                    },
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
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
            DropdownMenuItem(onClick = {
                menuExpanded = false
                randomAvatar()
            }) {
                Text(text = "修改头像", modifier = Modifier)
            }
            DropdownMenuItem(onClick = {
                menuExpanded = false
                quitGroup()
            }) {
                Text(text = "退出群聊", modifier = Modifier)
            }
        }
    }
}