package github.leavesczy.compose_chat.ui.main.friendship

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.main.friendship.logic.FriendshipPageViewState
import github.leavesczy.compose_chat.widgets.ComponentImage
import github.leavesczy.compose_chat.widgets.EmptyPage
import github.leavesczy.compose_chat.widgets.LoadingPage

@Composable
internal fun FriendshipPage(pageViewState: FriendshipPageViewState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FriendshipList(
            modifier = Modifier,
            pageViewState = pageViewState
        )
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(bottom = 30.dp, end = 30.dp),
            containerColor = AppTheme.colorScheme.c_FF5BA3F7_FF60A5FA.color,
            onClick = pageViewState.onClickShowFriendshipDialog
        ) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Filled.Favorite,
                tint = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
                contentDescription = stringResource(id = R.string.cd_add_friendship)
            )
        }
    }
}

@Composable
private fun FriendshipList(
    modifier: Modifier,
    pageViewState: FriendshipPageViewState
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = pageViewState.listState,
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        val loadError = pageViewState.loadError
        if (!loadError.isNullOrBlank()) {
            item(
                key = "LoadError",
                contentType = "LoadError"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        text = loadError,
                        fontSize = 14.sp,
                        color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color
                    )
                    Text(
                        modifier = Modifier
                            .clickable(onClick = pageViewState.onRetryLoad)
                            .padding(top = 8.dp),
                        text = stringResource(id = R.string.retry),
                        fontSize = 15.sp,
                        color = AppTheme.colorScheme.c_FF5BA3F7_FF60A5FA.color
                    )
                }
            }
        }
        val joinedGroupList = pageViewState.joinedGroupList
        val friendList = pageViewState.friendList
        if (joinedGroupList.isEmpty() && friendList.isEmpty() && loadError.isNullOrBlank()) {
            if (pageViewState.isLoading) {
                item(
                    key = "LoadingPage",
                    contentType = "LoadingPage"
                ) {
                    LoadingPage(modifier = Modifier)
                }
            } else {
                item(
                    key = "EmptyPage",
                    contentType = "EmptyPage"
                ) {
                    EmptyPage(modifier = Modifier)
                }
            }
        } else {
            items(
                items = joinedGroupList,
                key = { group ->
                    "group_${group.id}"
                },
                contentType = {
                    "FriendshipItem"
                }
            ) { group ->
                FriendshipItem(
                    modifier = Modifier
                        .animateItem(),
                    imageUrl = group.avatarUrl,
                    title = group.name,
                    subtitle = null,
                    onClick = {
                        pageViewState.onClickGroupItem(group)
                    }
                )
            }
            items(
                items = pageViewState.friendList,
                key = { friend ->
                    "friend_${friend.id}"
                },
                contentType = {
                    "FriendshipItem"
                }
            ) { friend ->
                FriendshipItem(
                    modifier = Modifier
                        .animateItem(),
                    imageUrl = friend.avatarUrl,
                    title = friend.showName,
                    subtitle = friend.signature,
                    onClick = {
                        pageViewState.onClickFriendItem(friend)
                    }
                )
            }
        }
    }
}

@Composable
private fun FriendshipItem(
    modifier: Modifier,
    imageUrl: String,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 70.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
    ) {
        ComponentImage(
            modifier = Modifier
                .size(size = 54.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp))
                .align(alignment = Alignment.CenterStart),
            model = imageUrl,
            contentDescription = stringResource(id = R.string.cd_avatar)
        )
        Column(
            modifier = Modifier
                .padding(start = 66.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 3.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            if (title.isNotBlank()) {
                Text(
                    modifier = Modifier,
                    text = title,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    modifier = Modifier,
                    text = subtitle,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd),
            thickness = 0.2.dp
        )
    }
}
