package github.leavesc.compose_chat.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import github.leavesc.compose_chat.utils.log

/**
 * @Author: leavesC
 * @Date: 2022/1/1 17:45
 * @Desc:
 */
@Composable
fun PreviewImageScreen(imagePath: String) {
    log {
        "imagePath: $imagePath"
    }
    Scaffold(
        modifier = Modifier,
        backgroundColor = MaterialTheme.colors.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            val imagePainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = imagePath)
                    .size(size = Size.ORIGINAL)
                    .placeholder(drawable = null)
                    .build(),
                filterQuality = FilterQuality.Medium,
            )
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = imagePainter,
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
        }
    }
}