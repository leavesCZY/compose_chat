package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
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
fun ZoomableComponentImage(
    modifier: Modifier = Modifier,
    model: Any?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.FillWidth,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null
) {
    ComponentImage(
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
    val animateFloat by rememberInfiniteTransition(label = "")
        .animateFloat(
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
        path.quadraticTo(width / 2 + width / 4 * animateValue, height, width, progress)
        path.lineTo(width, 0f)
        path.lineTo(0f, 0f)
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
        label = "",
        targetValue = if (animated) {
            1.6f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 1000)
    )
    val offset by animateIntOffsetAsState(
        label = "",
        targetValue = if (animated) {
            IntOffset(
                x = Random.nextInt(-200, 200),
                y = Random.nextInt(-200, 200)
            )
        } else {
            IntOffset(x = 0, y = 0)
        }
    )
    LaunchedEffect(key1 = key) {
        animated = true
        delay(timeMillis = 1000)
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
                IntOffset(x = offset.x.roundToInt(), y = offset.y.roundToInt())
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
                        offset = Offset(
                            x = offset.x + dragAmount.x,
                            y = offset.y + dragAmount.y
                        )
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
        model = model
    )
}