package github.leavesczy.compose_chat.ui.common

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import github.leavesczy.compose_chat.ui.theme.BackgroundColorDark
import github.leavesczy.compose_chat.utils.ImageUtils
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 17:45
 * @Desc:
 */
@Composable
fun PreviewImageScreen(imagePath: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun insertImageToAlbum() {
        coroutineScope.launch {
            val result = ImageUtils.insertImageToAlbum(context = context, data = imagePath)
            if (result) {
                showToast("图片已保存到相册")
            } else {
                showToast("图片保存失败")
            }
        }
    }

    val requestPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            insertImageToAlbum()
        } else {
            showToast("请先授予存储权限再保存图片")
        }
    }

    Scaffold(
        modifier = Modifier
            .background(color = BackgroundColorDark)
            .fillMaxSize(),
        containerColor = BackgroundColorDark,
        floatingActionButton = {
            IconButton(
                content = {
                    Icon(
                        imageVector = Icons.Filled.SaveAlt,
                        tint = Color.White,
                        contentDescription = null
                    )
                },
                onClick = {
                    if (ImageUtils.mustRequestWriteExternalStoragePermission(context = context)) {
                        requestPermissionLaunch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        insertImageToAlbum()
                    }
                })
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val imagePainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context = context)
                    .data(data = imagePath)
                    .size(size = Size.ORIGINAL)
                    .placeholder(drawable = null)
                    .build(),
                filterQuality = FilterQuality.Medium,
            )
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
                painter = imagePainter,
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
        }
    }
}