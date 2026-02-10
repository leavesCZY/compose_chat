package github.leavesczy.compose_chat.ui.chat.group

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.chat.group.logic.GroupProfilePageAction
import github.leavesczy.compose_chat.ui.chat.group.logic.GroupProfilePageViewState
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.AnimateBouncyImage
import github.leavesczy.compose_chat.ui.widgets.BezierImage
import github.leavesczy.compose_chat.ui.widgets.ComponentImage
import github.leavesczy.compose_chat.ui.widgets.ComposeDropdownMenuItem
import github.leavesczy.compose_chat.utils.randomImage
import kotlin.math.abs

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:20
 * @Desc:
 */

@Composable
internal fun GroupProfilePage(
    viewState: GroupProfilePageViewState,
    action: GroupProfilePageAction
) {
    val groupHeaderHeight = 280.dp
    val groupTopBarHeight = 44.dp
    val groupProfile = viewState.groupProfile
    if (groupProfile != null) {
        val listState = rememberLazyListState()
        val maxOffsetHeightPx = with(LocalDensity.current) {
            (groupHeaderHeight - groupTopBarHeight - 30.dp).roundToPx()
        }
        val topBarAlpha by remember {
            derivedStateOf {
                val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                if (visibleItemsInfo.isEmpty()) {
                    0f
                } else {
                    val first = visibleItemsInfo.first()
                    if (first.index == 0) {
                        val offset = abs(first.offset).coerceIn(0, maxOffsetHeightPx)
                        1.0f * offset / maxOffsetHeightPx
                    } else {
                        1.0f
                    }
                }
            }
        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF101010.color,
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(paddingValues = innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    item(
                        key = "header",
                        contentType = {
                            "header"
                        }
                    ) {
                        GroupHeader(
                            groupProfile = groupProfile,
                            groupHeaderHeight = groupHeaderHeight
                        )
                    }
                    items(
                        items = viewState.memberList,
                        key = {
                            it.detail.id
                        },
                        contentType = {
                            "GroupMemberItem"
                        },
                        itemContent = {
                            GroupMemberItem(
                                groupMemberProfile = it,
                                groupProfilePageAction = action
                            )
                        }
                    )
                }
                PageTopBar(
                    title = groupProfile.name,
                    alpha = topBarAlpha,
                    groupTopBarHeight = groupTopBarHeight,
                    action = action
                )
            }
        }
    }
}

@Composable
private fun GroupHeader(
    groupProfile: GroupProfile,
    groupHeaderHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = groupHeaderHeight)
    ) {
        val context = LocalContext.current
        val avatarUrl = groupProfile.faceUrl
        val introduction =
            "groupId: ${groupProfile.id}\ncreateTime: ${groupProfile.createTimeFormat}\n${groupProfile.introduction}"
        BezierImage(
            modifier = Modifier
                .fillMaxSize()
                .scrim(color = ComposeChatTheme.colorScheme.c_33000000_33000000.color),
            model = avatarUrl
        )
        Column(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 40.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AnimateBouncyImage(
                modifier = Modifier
                    .statusBarsPadding()
                    .size(size = 100.dp)
                    .clickableNoRipple {
                        if (avatarUrl.isNotBlank()) {
                            PreviewImageActivity.navTo(context = context, imageUri = avatarUrl)
                        }
                    },
                model = avatarUrl
            )
            Text(
                modifier = Modifier
                    .padding(all = 14.dp),
                text = introduction,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            )
        }
    }
}

@Composable
private fun PageTopBar(
    title: String,
    alpha: Float,
    groupTopBarHeight: Dp,
    action: GroupProfilePageAction
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .alpha(alpha = alpha)
                .fillMaxWidth()
                .background(color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF161616.color)
                .statusBarsPadding()
                .height(height = groupTopBarHeight)
        ) {
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.Center),
                text = title,
                fontSize = 20.sp,
                lineHeight = 21.sp,
                color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
            )
        }
        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .statusBarsPadding()
                .padding(start = 8.dp),
            onClick = {
                (context as Activity).finish()
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(size = 22.dp),
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }
        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .statusBarsPadding()
                .padding(end = 8.dp),
            onClick = {
                menuExpanded = true
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(size = 24.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.TopEnd)
            .padding(end = 20.dp)
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
                text = "修改头像",
                onClick = {
                    menuExpanded = false
                    action.setAvatar(randomImage())
                }
            )
            ComposeDropdownMenuItem(
                modifier = Modifier,
                text = "退出群聊",
                onClick = {
                    menuExpanded = false
                    action.quitGroup()
                }
            )
        }
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    groupProfilePageAction: GroupProfilePageAction
) {
    GroupMemberItem(
        modifier = Modifier,
        imageUrl = groupMemberProfile.detail.faceUrl,
        title = groupMemberProfile.detail.showName + "（${groupMemberProfile.detail.id}）",
        subtitle = "joinTime: ${groupMemberProfile.joinTimeFormat}",
        onClick = {
            groupProfilePageAction.onClickMember(groupMemberProfile)
        }
    )
}

@Composable
private fun GroupMemberItem(
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