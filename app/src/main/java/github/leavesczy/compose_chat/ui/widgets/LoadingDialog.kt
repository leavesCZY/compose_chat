package github.leavesczy.compose_chat.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun LoadingDialog(visible: Boolean) {
    if (visible) {
        BackHandler(enabled = true) {

        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickableNoRipple {}
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .size(size = 40.dp),
                color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
                strokeWidth = 3.dp
            )
        }
    }
}