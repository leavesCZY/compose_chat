package github.leavesczy.compose_chat.ui.chat

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.chat.group.GroupProfileActivity
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageViewState
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity

@Composable
internal fun ChatPageTopBar(
    modifier: Modifier,
    pageViewState: ChatPageViewState
) {
    val localContext = LocalContext.current
    val localActivity = LocalActivity.current
    val chat = pageViewState.chat
    val title = pageViewState.topBarTitle
    CenterAlignedTopAppBar(
        modifier = modifier
            .shadow(elevation = 0.8.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colorScheme.c_FFEAF2FF_FF0B1220.color),
        title = {
            Text(
                modifier = Modifier,
                text = title,
                fontSize = 19.sp,
                lineHeight = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
            )
        },
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 22.dp),
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = stringResource(id = R.string.cd_back)
                    )
                },
                onClick = {
                    localActivity?.finish()
                }
            )
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 24.dp),
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.cd_chat_profile)
                    )
                },
                onClick = {
                    when (chat) {
                        is Chat.C2C -> {
                            FriendProfileActivity.navTo(context = localContext, friendId = chat.id)
                        }

                        is Chat.Group -> {
                            GroupProfileActivity.navTo(context = localContext, groupId = chat.id)
                        }
                    }
                }
            )
        }
    )
}