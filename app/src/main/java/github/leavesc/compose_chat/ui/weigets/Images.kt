package github.leavesc.compose_chat.ui.weigets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.Coil
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import github.leavesc.compose_chat.ui.theme.BezierShape
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object CoilImageLoader {

    val ImageBackgroundColor = android.graphics.Color.parseColor("#1E000000")

    fun init(context: Context) {
        val bgColor =
            ColorDrawable(ImageBackgroundColor)
        Color.White.value
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(enable = true)
            .allowHardware(enable = false)
            .placeholder(drawable = bgColor)
            .error(drawable = bgColor)
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
            .scale(scale = Scale.FILL)
            .apply(block = builder)
            .build(),
        filterQuality = FilterQuality.Medium
    )
    Image(
        modifier = modifier.background(color = Color(color = CoilImageLoader.ImageBackgroundColor)),
        painter = imagePainter,
        contentScale = contentScale,
        contentDescription = null,
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
    borderWidth: Dp = 4.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
) {
    CoilImage(
        modifier = modifier
            .clip(shape = CircleShape)
            .border(width = borderWidth, color = borderColor, shape = CircleShape),
        data = data
    )
}

@Composable
fun BezierImage(modifier: Modifier, data: Any) {
    val animateValue by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    CoilImage(
        modifier = Modifier
            .scale(scale = (animateValue + 1f) * 1.3f)
            .clip(shape = BezierShape(animateValue = animateValue))
            .rotate(degrees = animateValue * 14f)
            .then(other = modifier),
        data = data
    )
}

@Composable
fun BouncyImage(modifier: Modifier, data: Any) {
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    fun launchDragAnimate() {
        coroutineScope.launch {
            Animatable(
                initialValue = Offset(x = offsetX, y = offsetY),
                typeConverter = Offset.VectorConverter
            ).animateTo(
                targetValue = Offset(x = 0f, y = 0f),
                animationSpec = SpringSpec(dampingRatio = Spring.DampingRatioHighBouncy),
                block = {
                    offsetX = value.x
                    offsetY = value.y
                }
            )
        }
    }
    CircleBorderImage(
        modifier = modifier
            .zIndex(zIndex = Float.MAX_VALUE)
            .offset {
                IntOffset(
                    x = offsetX.roundToInt(),
                    y = offsetY.roundToInt()
                )
            }
            .pointerInput(key1 = Unit) {
                detectDragGestures(
                    onDragStart = {

                    },
                    onDragCancel = {
                        launchDragAnimate()
                    },
                    onDragEnd = {
                        launchDragAnimate()
                    },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                )
            },
        data = data
    )
}