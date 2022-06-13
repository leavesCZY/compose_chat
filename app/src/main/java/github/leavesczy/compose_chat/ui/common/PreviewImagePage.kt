package github.leavesczy.compose_chat.ui.common

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.ui.theme.BackgroundColorDark
import github.leavesczy.compose_chat.ui.widgets.CoilImage
import github.leavesczy.compose_chat.ui.widgets.SystemBarColor
import github.leavesczy.compose_chat.utils.AlbumUtils
import github.leavesczy.compose_chat.utils.PermissionUtils
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 17:45
 * @Desc:
 */
@Composable
fun PreviewImagePage(imagePath: String) {
    val navHostController = LocalNavHostController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var resumed by remember {
        mutableStateOf(true)
    }
    SystemBarColor(
        appTheme = if (resumed) {
            AppTheme.Dark
        } else {
            AppThemeCache.currentTheme
        }
    )
    BackHandler(enabled = true) {
        resumed = false
        navHostController.popBackStack()
    }
    fun insertImageToAlbum() {
        coroutineScope.launch {
            val result = AlbumUtils.insertImageToAlbum(context = context, imageUrl = imagePath)
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
                    if (PermissionUtils.mustRequestWriteExternalStoragePermission(context = context)) {
                        requestPermissionLaunch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        insertImageToAlbum()
                    }
                })
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPadding),
            contentAlignment = Alignment.Center
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState()),
                data = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = imagePath)
                    .size(size = Size.ORIGINAL)
                    .scale(scale = Scale.FILL)
                    .build(),
                contentScale = ContentScale.FillWidth,
                backgroundColor = BackgroundColorDark
            )
        }
    }
}