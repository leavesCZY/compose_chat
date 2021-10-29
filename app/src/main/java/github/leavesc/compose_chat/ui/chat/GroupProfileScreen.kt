package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import github.leavesc.compose_chat.base.model.GroupMemberProfile
import github.leavesc.compose_chat.extend.viewModelInstance
import github.leavesc.compose_chat.logic.GroupProfileViewModel
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.profile.ProfileScreen
import github.leavesc.compose_chat.ui.weigets.CoilCircleImage
import github.leavesc.compose_chat.ui.weigets.CommonDivider

/**
 * @Author: leavesC
 * @Date: 2021/10/27 18:04
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun GroupProfileScreen(navController: NavHostController, groupId: String) {
    val groupProfileViewModel = viewModelInstance {
        GroupProfileViewModel(groupId = groupId)
    }
    val groupProfileScreenState by groupProfileViewModel.groupProfileScreenState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val onClickMember: (GroupMemberProfile) -> Unit = remember {
            object : (GroupMemberProfile) -> Unit {
                override fun invoke(member: GroupMemberProfile) {
                    navController.navigate(
                        route = Screen.FriendProfileScreen.generateRoute(friendId = member.userId)
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            item(key = true) {
                ProfileScreen(groupProfile = groupProfileScreenState.groupProfile)
            }
            val memberList = groupProfileScreenState.memberList
            memberList.forEach {
                item(key = it.userId) {
                    GroupMemberItem(groupMemberProfile = it, onClickMember = onClickMember)
                }
            }
        }
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    onClickMember: (GroupMemberProfile) -> Unit
) {
    val padding = 12.dp
    ConstraintLayout(
        modifier = Modifier
            .zIndex(zIndex = 10f)
            .fillMaxWidth()
            .clickable {
                onClickMember(groupMemberProfile)
            },
    ) {
        val (avatar, showName, role, divider) = createRefs()
        CoilCircleImage(
            data = groupMemberProfile.faceUrl,
            modifier = Modifier
                .padding(start = padding * 1.5f, top = padding, bottom = padding)
                .size(size = 50.dp)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        Text(
            text = groupMemberProfile.showName + "（ID: ${groupMemberProfile.userId}）",
            style = MaterialTheme.typography.subtitle1,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, top = padding, end = padding)
                .constrainAs(ref = showName) {
                    start.linkTo(anchor = avatar.end)
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        Text(
            text = groupMemberProfile.role + " - joinTime: ${groupMemberProfile.joinTimeFormat}",
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = role) {
                    start.linkTo(anchor = showName.start)
                    top.linkTo(anchor = showName.bottom, margin = padding / 2)
                    end.linkTo(anchor = parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = divider) {
                    start.linkTo(anchor = avatar.end, margin = padding)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = avatar.bottom)
                    width = Dimension.fillToConstraints
                }
        )
    }
}