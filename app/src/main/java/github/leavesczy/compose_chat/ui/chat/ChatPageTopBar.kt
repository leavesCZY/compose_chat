package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

/**
 * @Author: leavesCZY
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ChatPageTopBar(
    title: String,
    onClickBackMenu: (() -> Unit),
    onClickMoreMenu: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                modifier = Modifier,
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null
                )
            }, onClick = {
                onClickBackMenu()
            })
        },
        actions = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }, onClick = {
                onClickMoreMenu()
            })
        }
    )
}