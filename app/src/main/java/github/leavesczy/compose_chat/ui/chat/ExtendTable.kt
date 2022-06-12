package github.leavesczy.compose_chat.ui.chat

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.matisse.*

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 12:10
 * @Desc:
 */
@Composable
fun ExtendTable(selectPictureLauncher: ManagedActivityResultLauncher<Matisse, List<MediaResources>>) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .clickable {
                    val matisse = Matisse(
                        theme = if (AppThemeCache.currentTheme == AppTheme.Dark) {
                            DarkMatisseTheme
                        } else {
                            LightMatisseTheme
                        },
                        maxSelectable = 1,
                        captureStrategy = MediaStoreCaptureStrategy()
                    )
                    selectPictureLauncher.launch(matisse)
                }
                .padding(all = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(size = 45.dp),
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
}

