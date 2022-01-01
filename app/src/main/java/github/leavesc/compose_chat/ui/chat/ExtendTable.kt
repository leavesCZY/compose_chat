package github.leavesc.compose_chat.ui.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.R
import github.leavesc.compose_chat.common.SelectPictureContract
import github.leavesc.compose_chat.utils.BitmapUtils
import github.leavesc.compose_chat.utils.log
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2022/1/1 12:10
 * @Desc:
 */
@Composable
fun ExtendTable(sendImage: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 180.dp)
    ) {
        val launcher = rememberLauncherForActivityResult(
            contract = SelectPictureContract
        ) { imageUri ->
            log {
                "imageUri: " + imageUri?.toString()
            }
            if (imageUri != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    val imageFile = BitmapUtils.saveImage(context = context, imageUri = imageUri)
                    if (imageFile != null) {
                        sendImage(imageFile.absolutePath)
                    } else {
                        showToast("图片获取失败")
                    }
                }
            }
        }
        IconButton(onClick = {
            launcher.launch(Unit)
        }) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(size = 40.dp)
                        .padding(bottom = 6.dp),
                    painter = painterResource(id = R.drawable.icon_album),
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
                    contentDescription = null
                )
                Text(
                    text = "发送图片",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1.copy(fontSize = 13.sp)
                )
            }
        }
    }
}

