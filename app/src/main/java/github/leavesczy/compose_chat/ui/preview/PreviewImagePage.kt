package github.leavesczy.compose_chat.ui.preview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.core.content.ContextCompat
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.theme.AppTheme
import github.leavesczy.compose_chat.ui.widgets.ComponentImage
import kotlin.math.absoluteValue

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Composable
internal fun PreviewImagePage(
    imageUriList: List<String>,
    initialPage: Int,
    insertImageToAlbum: (imageUri: String) -> Unit
) {
    val localContext = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) {
        imageUriList.size
    }
    val requestPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            insertImageToAlbum(imageUriList[pagerState.currentPage])
        } else {
            ToastProvider.showToast(resId = R.string.toast_storage_permission_required)
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.c_FF22202A_FF22202A.color,
        contentWindowInsets = WindowInsets()
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
                pageSpacing = 6.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                Image(
                    modifier = Modifier,
                    pagerState = pagerState,
                    pageIndex = pageIndex,
                    imageUrl = imageUriList[pageIndex]
                )
            }
            DownloadIcon(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(all = 20.dp),
                onClick = {
                    val imageUrl = imageUriList[pagerState.currentPage]
                    if (mustRequestWriteExternalStoragePermission(context = localContext)) {
                        requestPermissionLaunch.launch(input = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        insertImageToAlbum(imageUrl)
                    }
                }
            )
        }
    }
}

@Composable
private fun Image(
    modifier: Modifier,
    pagerState: PagerState,
    pageIndex: Int,
    imageUrl: String
) {
    Box(
        modifier = modifier
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

@Composable
private fun DownloadIcon(
    modifier: Modifier,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        content = {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Filled.SaveAlt,
                tint = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
                contentDescription = null
            )
        },
        onClick = onClick
    )
}

private fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return false
    }
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}