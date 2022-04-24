package github.leavesczy.compose_chat.ui.chat

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 12:10
 * @Desc:
 */
@Composable
fun ExtendTable(
    selectPictureLauncher: ManagedActivityResultLauncher<Unit, Uri?>
) {
    Column(
        modifier = Modifier
            .padding(all = 20.dp)
            .clickable {
                selectPictureLauncher.launch(Unit)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(size = 45.dp),
            imageVector = Icons.Filled.InsertPhoto,
            contentDescription = null
        )
        Text(
            text = "发送图片",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

