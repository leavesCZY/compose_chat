package github.leavesczy.compose_chat.ui.preview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.app.ActivityCompat
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty
import github.leavesczy.compose_chat.ui.widgets.ComponentImage
import kotlin.math.absoluteValue

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:10
 * @Desc:
 */
@Composable
internal fun PreviewImagePage(
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
            .fillMaxSize(),
        containerColor = ComposeChatTheme.colorScheme.c_FF22202A_FF22202A.color,
        contentWindowInsets = WindowInsetsEmpty
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
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
                        tint = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
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
            ComponentImage(
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