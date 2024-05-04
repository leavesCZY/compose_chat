package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ComponentImage(
    modifier: Modifier = Modifier,
    model: Any?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null,
    backgroundColor: Color = Color(0x66888888)
) {
    AsyncImage(
        modifier = modifier
            .background(color = backgroundColor),
        model = model,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        contentDescription = contentDescription
    )
}

@Composable
fun ZoomableComponentImage(
    modifier: Modifier = Modifier,
    model: Any?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null
) {
    AsyncImage(
        modifier = modifier,
        model = model,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        contentDescription = contentDescription
    )
}

@Composable
fun BezierImage(modifier: Modifier, model: Any) {
    val animateFloat by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = ""
    )
    ComponentImage(
        modifier = Modifier
            .scale(scale = 1.0f + animateFloat * 0.1f)
            .clip(shape = BezierShape(animateValue = animateFloat))
            .then(other = modifier),
        model = model
    )
}

private class BezierShape(private val animateValue: Float) : Shape {

    private val path = Path()

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        path.reset()
        val width = size.width
        val height = size.height
        val progress = height / 7 * 5 + height / 7 * 2 * animateValue
        path.lineTo(0f, progress / 7 * 5)
        path.quadraticBezierTo(width / 2 + width / 4 * animateValue, height, width, progress)
        path.lineTo(width, 0f)
        path.lineTo(0f, 0f)
        return Outline.Generic(path = path)
    }

    override fun toString(): String = "BezierShape"

}

@Composable
fun BouncyImage(modifier: Modifier, data: Any) {
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember {
        mutableFloatStateOf(value = 0f)
    }
    var offsetY by remember {
        mutableFloatStateOf(value = 0f)
    }

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
    ComponentImage(
        modifier = modifier
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
                    }
                )
            }
            .clip(shape = CircleShape)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                shape = CircleShape
            )
            .zIndex(zIndex = Float.MAX_VALUE),
        model = data
    )
}