package github.leavesc.compose_chat.ui.weigets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.Coil
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import github.leavesc.compose_chat.extend.scrim
import github.leavesc.compose_chat.ui.theme.BezierShape

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

private val imageBackgroundColor = Color.DarkGray

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
        modifier = modifier.background(color = imageBackgroundColor),
        painter = imagePainter,
        contentDescription = null,
        contentScale = contentScale,
    )
}

@Composable
fun CircleImage(
    modifier: Modifier,
    data: Any,
) {
    CoilImage(
        modifier = modifier
            .clip(shape = CircleShape)
            .background(color = Color.DarkGray),
        data = data
    )
}

@Composable
fun CircleBorderImage(
    modifier: Modifier,
    data: Any,
    outlineSize: Dp = 4.dp,
    outlineColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
) {
    CoilImage(
        modifier = modifier
            .clip(shape = CircleShape)
            .border(width = outlineSize, color = outlineColor, shape = CircleShape),
        data = data
    )
}

@Composable
fun BezierImage(modifier: Modifier, data: Any) {
    val animateValue by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    CoilImage(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 5f / 4f)
            .zIndex(zIndex = -100f)
            .scale(scale = (animateValue + 1f) * 1.1f)
            .clip(shape = BezierShape(animateValue = animateValue))
            .rotate(degrees = animateValue * 10f)
            .scrim(colors = listOf(Color(color = 0x4D9DA3A8), Color(color = 0x41F7F5F5))),
        data = data
    )
}