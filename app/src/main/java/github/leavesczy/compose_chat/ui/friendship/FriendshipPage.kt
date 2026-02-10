package github.leavesczy.compose_chat.ui.friendship

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
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.conversation.EmptyPage
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipPageViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.ComponentImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipPage(pageViewState: FriendshipPageViewState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FriendshipPage(
            modifier = Modifier,
            pageViewState = pageViewState
        )
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(bottom = 30.dp, end = 30.dp),
            onClick = pageViewState.showFriendshipDialog
        )
    }
}

@Composable
private fun FriendshipPage(
    modifier: Modifier,
    pageViewState: FriendshipPageViewState
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = pageViewState.listState,
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        val joinedGroupList = pageViewState.joinedGroupList
        val friendList = pageViewState.friendList
        if (joinedGroupList.isEmpty() && friendList.isEmpty()) {
            item(
                key = "EmptyPage",
                contentType = "EmptyPage"
            ) {
                EmptyPage(modifier = Modifier)
            }
        } else {
            items(
                items = joinedGroupList,
                key = {
                    it.id
                },
                contentType = {
                    "FriendshipItem"
                }
            ) {
                FriendshipItem(
                    modifier = Modifier
                        .animateItem(),
                    imageUrl = it.faceUrl,
                    title = it.name,
                    subtitle = null,
                    onClick = {
                        pageViewState.onClickGroupItem(it)
                    }
                )
            }
            items(
                items = pageViewState.friendList,
                key = {
                    it.id
                },
                contentType = {
                    "FriendshipItem"
                }
            ) {
                FriendshipItem(
                    modifier = Modifier
                        .animateItem(),
                    imageUrl = it.faceUrl,
                    title = it.showName,
                    subtitle = it.signature,
                    onClick = {
                        pageViewState.onClickFriendItem(it)
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
            model = imageUrl
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
                    color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
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
                    color = ComposeChatTheme.colorScheme.c_FF384F60_99FFFFFF.color
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

@Composable
private fun FloatingActionButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier
            .size(size = 50.dp),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
        containerColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
        content = {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Filled.Favorite,
                tint = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
                contentDescription = null
            )
        },
        onClick = onClick
    )
}