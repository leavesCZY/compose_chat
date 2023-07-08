package github.leavesczy.compose_chat.ui.friendship

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipPageViewState
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipViewModel
import github.leavesczy.compose_chat.ui.widgets.CoilImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipPage(showFriendshipDialog: () -> Unit) {
    val friendshipViewModel = viewModel<FriendshipViewModel>()
    FriendshipContentPage(
        pageViewState = friendshipViewModel.pageViewState,
        showFriendshipDialog = showFriendshipDialog
    )
}

@Composable
private fun FriendshipContentPage(
    pageViewState: FriendshipPageViewState,
    showFriendshipDialog: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val joinedGroupList = pageViewState.joinedGroupList
        val friendList = pageViewState.friendList
        if (joinedGroupList.isEmpty() && friendList.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.45f)
                    .wrapContentSize(align = Alignment.BottomCenter),
                text = "Empty",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 70.sp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = pageViewState.listState,
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(
                    items = joinedGroupList,
                    key = {
                        it.id
                    },
                    contentType = {
                        "Group"
                    },
                    itemContent = {
                        GroupItem(
                            groupProfile = it,
                            onClick = {
                                pageViewState.onClickGroupItem(it)
                            }
                        )
                    }
                )
                items(
                    items = friendList,
                    key = {
                        it.id
                    },
                    contentType = {
                        "friend"
                    },
                    itemContent = {
                        FriendItem(
                            personProfile = it,
                            onClick = {
                                pageViewState.onClickFriendItem(it)
                            }
                        )
                    }
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(bottom = 30.dp, end = 30.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            content = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    tint = Color.White,
                    contentDescription = null,
                )
            },
            onClick = showFriendshipDialog
        )
    }
}

@Composable
private fun LazyItemScope.GroupItem(
    groupProfile: GroupProfile,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        val (avatarRef, showNameRef, dividerRef) = createRefs()
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                }
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                .size(size = 50.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            data = groupProfile.faceUrl
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
                    linkTo(top = parent.top, bottom = parent.bottom)
                    width = Dimension.fillToConstraints
                },
            text = groupProfile.name,
            fontSize = 17.sp,
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
private fun LazyItemScope.FriendItem(
    personProfile: PersonProfile,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        val (avatarRef, showNameRef, signatureRef, dividerRef) = createRefs()
        val verticalChain = createVerticalChain(
            showNameRef, signatureRef, chainStyle = ChainStyle.Packed
        )
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
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                .size(size = 50.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            data = personProfile.faceUrl
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
                .padding(bottom = 1.dp),
            text = personProfile.showName,
            fontSize = 17.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = signatureRef) {
                    linkTo(
                        start = showNameRef.start,
                        end = parent.end,
                        endMargin = 12.dp
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 1.dp),
            text = personProfile.signature,
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