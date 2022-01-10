package github.leavesc.compose_chat.ui.weigets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
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

object CoilImageLoader {

    fun init(context: Context) {
        val bgColor = ColorDrawable(android.graphics.Color.parseColor("#1E000000"))
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .allowHardware(false)
            .placeholder(bgColor)
            .error(bgColor)
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
        model = ImageRequest.Builder(context = LocalContext.current)
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
fun CircleCoilImage(
    modifier: Modifier,
    data: Any,
) {
    CoilImage(
        modifier = modifier.clip(shape = CircleShape),
        data = data
    )
}

@Composable
fun CircleBorderCoilImage(
    modifier: Modifier,
    data: Any,
    outlineSize: Dp = 4.dp,
    outlineColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
) {
    CoilImage(
        modifier = modifier
            .clip(shape = CircleShape)
            .border(outlineSize, outlineColor, CircleShape),
        data = data
    )
}