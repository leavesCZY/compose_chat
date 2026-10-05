package github.leavesczy.compose_chat.ui.preview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.content.ContextCompat
import coil3.request.ImageRequest
import coil3.size.Size
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.widgets.ComponentImage
import kotlin.math.absoluteValue

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
        containerColor = AppTheme.colorScheme.c_FF0B1220_FF0B1220.color,
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
                .fillMaxSize()
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
                }
        ) {
            PreviewImageContent(imageUrl = imageUrl)
        }
    }
}

@Composable
private fun PreviewImageContent(imageUrl: String) {
    val localContext = LocalContext.current
    val imageRequest = remember(key1 = imageUrl) {
        ImageRequest.Builder(context = localContext)
            .data(data = imageUrl)
            .size(size = Size.ORIGINAL)
            .build()
    }
    var imageRatio by remember(key1 = imageUrl) {
        mutableStateOf<Float?>(value = null)
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val ratio = imageRatio
        val imageHeight = if (ratio != null && ratio > 0f) {
            maxWidth / ratio
        } else {
            null
        }
        val scrollEnabled = imageHeight != null && imageHeight > maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = scrollEnabled
                ),
            verticalArrangement = if (scrollEnabled) {
                Arrangement.Top
            } else {
                Arrangement.Center
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComponentImage(
                modifier = if (ratio == null) {
                    Modifier
                        .fillMaxSize()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = ratio)
                },
                model = imageRequest,
                contentScale = ContentScale.FillWidth,
                onSuccess = { state ->
                    val image = state.result.image
                    if (image.width > 0 && image.height > 0) {
                        imageRatio = image.width.toFloat() / image.height.toFloat()
                    }
                }
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
                contentDescription = stringResource(id = R.string.cd_save_image)
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