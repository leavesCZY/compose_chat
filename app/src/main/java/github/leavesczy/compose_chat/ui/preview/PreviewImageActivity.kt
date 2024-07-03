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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty
import github.leavesczy.compose_chat.ui.widgets.ZoomableComponentImage
import github.leavesczy.compose_chat.utils.AlbumUtils
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class PreviewImageActivity : BaseActivity() {

    companion object {

        private const val KEY_IMAGE_URI_LIST = "keyImageUriList"

        private const val KEY_INITIAL_PAGE = "keyInitialPage"

        fun navTo(context: Context, imageUri: String) {
            navTo(context = context, imageUriList = listOf(element = imageUri), initialPage = 0)
        }

        fun navTo(context: Context, imageUriList: List<String>, initialPage: Int) {
            val intent = Intent(context, PreviewImageActivity::class.java)
            intent.putStringArrayListExtra(KEY_IMAGE_URI_LIST, arrayListOf<String>().apply {
                addAll(imageUriList)
            })
            intent.putExtra(KEY_INITIAL_PAGE, initialPage)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val imageUriList by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringArrayListExtra(KEY_IMAGE_URI_LIST)?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    private val initialPage by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getIntExtra(KEY_INITIAL_PAGE, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreviewImagePage(
                imageUriList = imageUriList,
                initialPage = initialPage,
                insertImageToAlbum = ::insertImageToAlbum
            )
        }
    }

    @Composable
    override fun SetSystemBarUi() {
        val context = LocalContext.current
        LaunchedEffect(key1 = null) {
            if (context is Activity) {
                val window = context.window
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    private fun insertImageToAlbum(imageUri: String) {
        lifecycleScope.launch {
            val result = AlbumUtils.insertImageToAlbum(
                context = applicationContext,
                imageUri = imageUri
            )
            if (result) {
                showToast(msg = "已保存到相册")
            } else {
                showToast(msg = "保存失败")
            }
        }
    }

}

@Composable
private fun PreviewImagePage(
    imageUriList: List<String>,
    initialPage: Int,
    insertImageToAlbum: (String) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) {
        imageUriList.size
    }
    val requestPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            insertImageToAlbum(imageUriList[pagerState.currentPage])
        } else {
            ToastProvider.showToast(context = context, msg = "请先授予存储权限再保存图片")
        }
    }
    Scaffold(
        modifier = Modifier
            .background(color = Color(color = 0xFF22202A))
            .fillMaxSize(),
        containerColor = Color(color = 0xFF22202A),
        contentWindowInsets = WindowInsetsEmpty
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                pageSpacing = 0.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                PreviewPage(
                    pagerState = pagerState,
                    pageIndex = pageIndex,
                    imageUrl = imageUriList[pageIndex]
                )
            }
            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(all = 20.dp),
                content = {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Filled.SaveAlt,
                        tint = Color.White,
                        contentDescription = null
                    )
                },
                onClick = {
                    val imageUrl = imageUriList[pagerState.currentPage]
                    if (mustRequestWriteExternalStoragePermission(context = context)) {
                        requestPermissionLaunch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        insertImageToAlbum(imageUrl)
                    }
                }
            )
        }
    }
}

@Composable
private fun PreviewPage(
    pagerState: PagerState,
    pageIndex: Int,
    imageUrl: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset =
                        ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction).absoluteValue
                    val fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    lerp(
                        start = 0.84f,
                        stop = 1f,
                        fraction = fraction
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = fraction
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            ZoomableComponentImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = imageUrl,
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

private fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return false
    }
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}