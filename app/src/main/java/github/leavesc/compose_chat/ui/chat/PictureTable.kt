package github.leavesc.compose_chat.ui.chat

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
fun PictureTable(sendImage: (String) -> Unit) {
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
            Log.e("imageUri", imageUri.toString())
            if (imageUri != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    val imageFile = BitmapUtils.saveImage(context = context, imageUri = imageUri)
                    if (imageFile != null) {
                        sendImage(imageFile.absolutePath)
                    }
                }
            }
        }
        Button(
            modifier = Modifier.padding(all = 40.dp),
            onClick = {
                launcher.launch(Unit)
            }) {
            Text(text = "发送图片")
        }
    }
}

