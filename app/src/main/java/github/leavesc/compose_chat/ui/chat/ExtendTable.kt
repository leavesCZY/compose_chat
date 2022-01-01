package github.leavesc.compose_chat.ui.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.R
import github.leavesc.compose_chat.common.SelectPictureContract
import github.leavesc.compose_chat.utils.BitmapUtils
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
            if (imageUri != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    val imageFile = BitmapUtils.saveImage(context = context, imageUri = imageUri)
                    if (imageFile != null) {
                        sendImage(imageFile.absolutePath)
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
                    .padding(start = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(size = 60.dp),
                    painter = painterResource(id = R.drawable.icon_album),
                    contentDescription = null,
                    alignment = Alignment.Center,
                )
                Text(text = "发送图片", fontSize = 16.sp)
            }
        }
    }
}

