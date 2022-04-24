package github.leavesczy.compose_chat.ui.friend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.model.FriendshipScreenState
import github.leavesczy.compose_chat.ui.widgets.CircleImage
import github.leavesczy.compose_chat.ui.widgets.CommonDivider
import github.leavesczy.compose_chat.ui.widgets.EmptyView

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:55
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipScreen(
    paddingValues: PaddingValues,
    friendshipScreenState: FriendshipScreenState,
) {
    Scaffold(
        modifier = Modifier
            .padding(paddingValues = paddingValues)
            .fillMaxSize()
    ) {
        if (friendshipScreenState.joinedGroupList.isEmpty() && friendshipScreenState.friendList.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(
                state = friendshipScreenState.listState,
                contentPadding = PaddingValues(bottom = 60.dp),
            ) {
                val joinedGroupList = friendshipScreenState.joinedGroupList
                val friendList = friendshipScreenState.friendList
                joinedGroupList.forEach {
                    item(key = it.id) {
                        GroupItem(
                            groupProfile = it,
                            onClickGroup = friendshipScreenState.onClickGroup
                        )
                    }
                }
                friendList.forEach {
                    item(key = it.id) {
                        FriendItem(
                            personProfile = it,
                            onClickFriend = friendshipScreenState.onClickFriend
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupItem(groupProfile: GroupProfile, onClickGroup: (GroupProfile) -> Unit) {
    val padding = 12.dp
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClickGroup(groupProfile)
            },
    ) {
        val (avatar, showName, divider) = createRefs()
        CircleImage(
            data = groupProfile.faceUrl,
            modifier = Modifier
                .padding(start = padding * 1.5f, top = padding, bottom = padding)
                .size(size = 50.dp)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        Text(
            text = groupProfile.name,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = showName) {
                    start.linkTo(anchor = avatar.end)
                    top.linkTo(anchor = parent.top)
                    bottom.linkTo(anchor = parent.bottom)
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

@Composable
private fun FriendItem(personProfile: PersonProfile, onClickFriend: (PersonProfile) -> Unit) {
    val padding = 12.dp
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClickFriend(personProfile)
            },
    ) {
        val (avatar, showName, signature, divider) = createRefs()
        CircleImage(
            data = personProfile.faceUrl,
            modifier = Modifier
                .padding(start = padding, top = padding, bottom = padding)
                .size(size = 50.dp)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        Text(
            text = personProfile.showName,
            style = MaterialTheme.typography.titleMedium,
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
            text = personProfile.signature,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = signature) {
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