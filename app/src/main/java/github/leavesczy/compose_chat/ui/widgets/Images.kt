package github.leavesczy.compose_chat.ui.widgets

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.compose.animation.core.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import github.leavesczy.compose_chat.ui.theme.BezierShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/**
 * @Author: leavesCZY
 * @Date: 2022/5/13 11:25
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object CoilImageLoader {

    fun init(application: Application) {
        val imageLoader = ImageLoader.Builder(context = application)
            .crossfade(enable = false)
            .allowHardware(enable = false)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }

    suspend fun getCachedFileOrDownload(context: Context, imageUrl: String): File? {
        return withContext(context = Dispatchers.IO) {
            val file = getCachedFile(context = context, imageUrl = imageUrl)
            if (file != null) {
                return@withContext file
            }
            val request = ImageRequest.Builder(context = context)
                .data(data = imageUrl)
                .build()
            val imageResult = context.imageLoader.execute(request = request)
            if (imageResult is SuccessResult) {
                return@withContext getCachedFile(context = context, imageUrl = imageUrl)
            }
            return@withContext null
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun getCachedFile(context: Context, imageUrl: String): File? {
        val snapshot = context.imageLoader.diskCache?.get(imageUrl)
        val file = snapshot?.data?.toFile()
        snapshot?.close()
        if (file != null && file.exists()) {
            return file
        }
        return null
    }

}

@Composable
fun CoilImage(
    modifier: Modifier,
    data: Any,
    contentScale: ContentScale = ContentScale.Crop,
    filterQuality: FilterQuality = FilterQuality.Low,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.4f)
) {
    AsyncImage(
        modifier = modifier.background(color = backgroundColor),
        model = data,
        contentScale = contentScale,
        filterQuality = filterQuality,
        contentDescription = null
    )
}

@Composable
fun CircleBorderImage(
    modifier: Modifier,
    data: Any,
    borderWidth: Dp = 2.dp,
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
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                )
            },
        data = data
    )
}