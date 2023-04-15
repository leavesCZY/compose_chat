package github.leavesczy.compose_chat.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.extend.clickableNoRipple

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun LoadingDialog(visible: Boolean) {
    BackHandler(enabled = visible, onBack = {

    })
    if (visible) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickableNoRipple {

            }) {
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}