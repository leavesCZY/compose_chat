package github.leavesc.compose_chat.ui.weigets

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.Coil
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import github.leavesc.compose_chat.R

object CoilImageLoader {

    fun init(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .allowHardware(false)
            .placeholder(R.drawable.icon_logo_round)
            .build()
        Coil.setImageLoader(imageLoader)
    }

}

@Composable
fun CoilImage(
    modifier: Modifier = Modifier,
    data: Any,
    contentScale: ContentScale = ContentScale.Crop,
    builder: ImageRequest.Builder.() -> Unit = {},
) {
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data = data)
            .scale(scale = Scale.FIT)
            .apply(block = builder)
            .build(),
        filterQuality = FilterQuality.Medium
    )
    Image(
        modifier = modifier,
        painter = imagePainter,
        contentDescription = null,
        contentScale = contentScale,
    )
}

@Composable
fun CoilCircleImage(
    modifier: Modifier = Modifier,
    data: Any,
) {
    CoilImage(
        modifier = modifier.clip(shape = CircleShape),
        data = data,
        builder = {
//            transformations(CircleCropTransformation())
        }
    )
}

@Composable
fun OutlinedAvatar(
    modifier: Modifier = Modifier,
    data: String,
    outlineSize: Dp = 4.dp,
    outlineColor: Color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f)
) {
    Box(
        modifier = modifier.background(
            color = outlineColor,
            shape = CircleShape
        ),
        contentAlignment = Alignment.Center,
    ) {
        CoilCircleImage(
            data = data,
            modifier = Modifier
                .padding(outlineSize)
                .fillMaxSize()
        )
    }
}