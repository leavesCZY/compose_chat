package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.model.Chat
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
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier,
                text = title,
                fontSize = 19.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
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