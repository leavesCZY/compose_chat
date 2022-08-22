package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.common.model.GroupProfile
import github.leavesczy.compose_chat.common.model.PersonProfile
import github.leavesczy.compose_chat.model.FriendshipPageViewState
import github.leavesczy.compose_chat.model.MainPageAction
import github.leavesczy.compose_chat.ui.widgets.CoilImage
import github.leavesczy.compose_chat.ui.widgets.CommonDivider
import github.leavesczy.compose_chat.ui.widgets.EmptyView

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:55
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipPage(
    viewState: FriendshipPageViewState,
    mainPageAction: MainPageAction
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        if (viewState.joinedGroupList.isEmpty() && viewState.friendList.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(
                state = viewState.listState,
                contentPadding = PaddingValues(bottom = 60.dp),
            ) {
                val joinedGroupList = viewState.joinedGroupList
                val friendList = viewState.friendList
                items(items = joinedGroupList, key = {
                    it.id
                }, contentType = {
                    "Group"
                }, itemContent = {
                    GroupItem(
                        groupProfile = it,
                        onClickGroupItem = mainPageAction.onClickGroupItem
                    )
                })
                items(items = friendList, key = {
                    it.id
                }, contentType = {
                    "friend"
                }, itemContent = {
                    FriendItem(
                        personProfile = it,
                        onClickFriendItem = mainPageAction.onClickFriendItem
                    )
                })
            }
        }
    }
}

@Composable
private fun LazyItemScope.GroupItem(
    groupProfile: GroupProfile,
    onClickGroupItem: (GroupProfile) -> Unit
) {
    val avatarSize = 55.dp
    val padding = 8.dp
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable {
                onClickGroupItem(groupProfile)
            },
    ) {
        val (avatarRef, showNameRef, dividerRef) = createRefs()
        CoilImage(
            modifier = Modifier
                .padding(horizontal = padding * 1.5f, vertical = padding)
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
            data = groupProfile.faceUrl
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRef) {
                    linkTo(start = avatarRef.end, end = parent.end, endMargin = padding)
                    linkTo(top = parent.top, bottom = parent.bottom)
                    width = Dimension.fillToConstraints
                },
            text = groupProfile.name,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = dividerRef) {
                    linkTo(start = avatarRef.end, end = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
private fun LazyItemScope.FriendItem(
    personProfile: PersonProfile,
    onClickFriendItem: (PersonProfile) -> Unit
) {
    val avatarSize = 55.dp
    val padding = 8.dp
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable {
                onClickFriendItem(personProfile)
            },
    ) {
        val (avatarRef, showNameRef, signatureRef, dividerRef) = createRefs()
        val verticalChain =
            createVerticalChain(showNameRef, signatureRef, chainStyle = ChainStyle.Packed)
        constrain(ref = verticalChain) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        CoilImage(
            modifier = Modifier
                .padding(horizontal = padding * 1.5f, vertical = padding)
                .size(size = avatarSize)
                .clip(shape = CircleShape)
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
            data = personProfile.faceUrl
        )
        Text(
            modifier = Modifier
                .padding(bottom = 2.dp, end = padding)
                .constrainAs(ref = showNameRef) {
                    linkTo(
                        start = avatarRef.end,
                        end = parent.end,
                        endMargin = padding
                    )
                    width = Dimension.fillToConstraints
                },
            text = personProfile.showName,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .padding(top = 2.dp, end = padding)
                .constrainAs(ref = signatureRef) {
                    linkTo(start = avatarRef.end, end = parent.end, endMargin = padding)
                    width = Dimension.fillToConstraints
                },
            text = personProfile.signature,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = dividerRef) {
                    linkTo(start = avatarRef.end, end = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                    width = Dimension.fillToConstraints
                }
        )
    }
}