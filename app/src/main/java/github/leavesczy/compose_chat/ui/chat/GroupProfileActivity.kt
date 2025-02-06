package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.MainActivity
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfilePageAction
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfilePageViewState
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.AnimateBouncyImage
import github.leavesczy.compose_chat.ui.widgets.ComponentImage
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import github.leavesczy.compose_chat.utils.randomFaceUrl
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileActivity : BaseActivity() {

    companion object {

        private const val KEY_GROUP_ID = "keyGroupId"

        fun navTo(context: Context, groupId: String) {
            val intent = Intent(context, GroupProfileActivity::class.java)
            intent.putExtra(KEY_GROUP_ID, groupId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val groupId by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringExtra(KEY_GROUP_ID) ?: ""
    }

    private val groupProfileViewModel by viewModelsInstance {
        GroupProfileViewModel(groupId = groupId)
    }

    private val groupProfilePageAction = GroupProfilePageAction(
        setAvatar = {
            groupProfileViewModel.setAvatar(avatarUrl = it)
        },
        quitGroup = {
            lifecycleScope.launch {
                when (val result = groupProfileViewModel.quitGroup()) {
                    ActionResult.Success -> {
                        showToast(msg = "已退出群聊")
                        startActivity(Intent(this@GroupProfileActivity, MainActivity::class.java))
                    }

                    is ActionResult.Failed -> {
                        showToast(msg = result.reason)
                    }
                }
            }
        },
        onClickMember = {
            FriendProfileActivity.navTo(
                context = this,
                friendId = it.detail.id
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupProfilePage(
                viewState = groupProfileViewModel.pageViewState,
                action = groupProfilePageAction
            )
            LoadingDialog(visible = groupProfileViewModel.loadingDialogVisible)
        }
    }

}

private val groupHeaderHeight = 280.dp

private val groupTopBarHeight = 44.dp

@Composable
private fun GroupProfilePage(
    viewState: GroupProfilePageViewState,
    action: GroupProfilePageAction
) {
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
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 60.dp),
                ) {
                    item(
                        key = "header",
                        contentType = {
                            "header"
                        }
                    ) {
                        GroupHeader(groupProfile = groupProfile)
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
                    action = action
                )
            }
        }
    }
}

@Composable
private fun GroupHeader(groupProfile: GroupProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = groupHeaderHeight)
    ) {
        val context = LocalContext.current
        val avatarUrl = groupProfile.faceUrl
        val introduction =
            "groupID: ${groupProfile.id}\ncreateTime: ${groupProfile.createTimeFormat}\n${groupProfile.introduction}"
        ComponentImage(
            modifier = Modifier
                .fillMaxSize()
                .scrim(color = Color(0x4D000000)),
            model = avatarUrl
        )
        Column(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 40.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
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
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PageTopBar(
    title: String,
    alpha: Float,
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
                .background(color = MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .height(height = groupTopBarHeight)
        ) {
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.Center),
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
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
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "修改头像",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                onClick = {
                    menuExpanded = false
                    action.setAvatar(randomFaceUrl())
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "退出群聊",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 70.dp)
            .clickable {
                groupProfilePageAction.onClickMember(groupMemberProfile)
            }
    ) {
        ComponentImage(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .padding(horizontal = 14.dp)
                .size(size = 50.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            model = groupMemberProfile.detail.faceUrl
        )
        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .padding(start = 78.dp, end = 12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                modifier = Modifier,
                text = groupMemberProfile.detail.showName + "（${groupMemberProfile.detail.id}）",
                fontSize = 17.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                modifier = Modifier,
                text = "joinTime: ${groupMemberProfile.joinTimeFormat}",
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(start = 78.dp),
            thickness = 0.2.dp
        )
    }
}