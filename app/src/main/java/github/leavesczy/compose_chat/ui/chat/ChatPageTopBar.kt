package github.leavesczy.compose_chat.ui.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

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
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier,
                text = title,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
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