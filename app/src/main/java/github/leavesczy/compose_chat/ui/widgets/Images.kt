package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import coil3.compose.SubcomposeAsyncImage
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

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
    SubcomposeAsyncImage(
        modifier = modifier,
        model = model,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        contentDescription = contentDescription,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor)
            )
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor)
            )
        }
    )
}

@Composable
fun BezierImage(modifier: Modifier, model: Any) {
    val animateFloat by rememberInfiniteTransition()
        .animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    ComponentImage(
        modifier = Modifier
            .clip(shape = BezierShape(animateValue = animateFloat))
            .then(other = modifier),
        model = model
    )
}

private class BezierShape(private val animateValue: Float) : Shape {

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        val waveHeight = 20
        val waveFrequency = 0.2
        val path = Path()
        path.reset()
        path.moveTo(size.width, size.height)
        path.lineTo(size.width, 0f)
        path.lineTo(0f, 0f)
        path.lineTo(0f, size.height)
        var i = 0f
        while (i < size.width) {
            val y =
                size.height - (waveHeight * sin((i / size.width * 2 * Math.PI * waveFrequency) + (animateValue * 2 * Math.PI)))
            path.lineTo(i, y.toFloat())
            i++
        }
        path.lineTo(size.width, size.height)
        path.close()
        return Outline.Generic(path = path)
    }

    override fun toString(): String = "BezierShape"

}

@Composable
fun AnimateBouncyImage(
    modifier: Modifier,
    model: String,
    key: Any = Unit
) {
    var animated by remember {
        mutableStateOf(value = false)
    }
    val scale by animateFloatAsState(
        targetValue = if (animated) {
            1.5f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 1200)
    )
    val offset by animateIntOffsetAsState(
        targetValue = if (animated) {
            IntOffset(
                x = Random.nextInt(-500, 500),
                y = Random.nextInt(-200, 300)
            )
        } else {
            IntOffset(x = 0, y = 0)
        }
    )
    LaunchedEffect(key1 = key) {
        animated = true
        delay(timeMillis = 1000L)
        animated = false
    }
    BouncyImage(
        modifier = modifier
            .offset {
                offset
            }
            .scale(scale = scale),
        model = model
    )
}

@Composable
private fun BouncyImage(modifier: Modifier, model: Any) {
    val coroutineScope = rememberCoroutineScope()
    var offset by remember {
        mutableStateOf(value = Offset.Zero)
    }

    fun launchDragAnimate() {
        coroutineScope.launch {
            Animatable(
                initialValue = Offset(x = offset.x, y = offset.y),
                typeConverter = Offset.VectorConverter
            ).animateTo(
                targetValue = Offset.Zero,
                animationSpec = SpringSpec(dampingRatio = Spring.DampingRatioHighBouncy),
                block = {
                    offset = Offset(x = value.x, y = value.y)
                }
            )
        }
    }
    ComponentImage(
        modifier = modifier
            .offset {
                IntOffset(
                    x = offset.x.roundToInt(),
                    y = offset.y.roundToInt()
                )
            }
            .clip(shape = CircleShape)
            .border(
                width = 2.dp,
                color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(alpha = 0.8f),
                shape = CircleShape
            )
            .zIndex(zIndex = Float.MAX_VALUE)
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
                        offset = Offset(
                            x = offset.x + dragAmount.x,
                            y = offset.y + dragAmount.y
                        )
                    }
                )
            },
        model = model
    )
}