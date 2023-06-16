package github.leavesczy.compose_chat.ui.preview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.logic.AppTheme
import github.leavesczy.compose_chat.ui.theme.BackgroundColorDark
import github.leavesczy.compose_chat.ui.theme.DarkColorScheme
import github.leavesczy.compose_chat.ui.widgets.CoilImage
import github.leavesczy.compose_chat.ui.widgets.SystemBarTheme
import github.leavesczy.compose_chat.utils.AlbumUtils
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class PreviewImageActivity : BaseActivity() {

    companion object {

        private const val keyImagePath = "keyImagePath"

        fun navTo(context: Context, imagePath: String) {
            val intent = Intent(context, PreviewImageActivity::class.java)
            intent.putExtra(keyImagePath, imagePath)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val imagePath by lazy {
        intent.getStringExtra(keyImagePath) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(
            systemBarTheme = {
                SystemBarTheme(
                    appTheme = AppTheme.Dark,
                    navigationBarColor = DarkColorScheme.background
                )
            }
        ) {
            PreviewImagePage(imagePath = imagePath)
        }
    }

}

@Composable
private fun PreviewImagePage(imagePath: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    fun insertImageToAlbum() {
        coroutineScope.launch {
            val result = AlbumUtils.insertImageToAlbum(context = context, imageUrl = imagePath)
            if (result) {
                ToastProvider.showToast(msg = "图片已保存到相册")
            } else {
                ToastProvider.showToast(msg = "图片保存失败")
            }
        }
    }

    val requestPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            insertImageToAlbum()
        } else {
            ToastProvider.showToast(msg = "请先授予存储权限再保存图片")
        }
    }

    Scaffold(
        modifier = Modifier
            .background(color = BackgroundColorDark)
            .fillMaxSize(),
        containerColor = BackgroundColorDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState()),
                data = ImageRequest
                    .Builder(context = LocalContext.current)
                    .data(data = imagePath)
                    .size(size = Size.ORIGINAL)
                    .scale(scale = Scale.FILL)
                    .build(),
                contentScale = ContentScale.FillWidth,
                backgroundColor = BackgroundColorDark
            )
            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(all = 20.dp),
                content = {
                    Icon(
                        imageVector = Icons.Filled.SaveAlt,
                        tint = Color.White,
                        contentDescription = null
                    )
                },
                onClick = {
                    if (mustRequestWriteExternalStoragePermission(context = context)) {
                        requestPermissionLaunch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        insertImageToAlbum()
                    }
                }
            )
        }
    }
}

private fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return false
    }
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}