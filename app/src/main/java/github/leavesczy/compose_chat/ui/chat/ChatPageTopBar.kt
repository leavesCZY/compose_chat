package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ChatPageTopBar(title: String, chat: Chat) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        modifier = Modifier
            .shadow(elevation = 0.8.dp),
        title = {
            Text(
                modifier = Modifier,
                text = title,
                fontSize = 19.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 22.dp),
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null
                    )
                },
                onClick = {
                    (context as Activity).finish()
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
                        contentDescription = null
                    )
                },
                onClick = {
                    when (chat) {
                        is Chat.PrivateChat -> {
                            FriendProfileActivity.navTo(context = context, friendId = chat.id)
                        }

                        is Chat.GroupChat -> {
                            GroupProfileActivity.navTo(context = context, groupId = chat.id)
                        }
                    }
                }
            )
        }
    )
}