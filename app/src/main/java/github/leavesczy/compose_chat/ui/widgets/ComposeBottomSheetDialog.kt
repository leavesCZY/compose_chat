package github.leavesczy.compose_chat.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import github.leavesczy.compose_chat.extend.clickableNoRipple
import kotlin.math.roundToInt

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ComposeBottomSheetDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    BackHandler(enabled = visible) {
        if (cancelable) {
            onDismissRequest()
        }
    }
    val animationDuration = 320
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearEasing
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearEasing
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0x99000000))
                    .clickableNoRipple {
                        if (canceledOnTouchOutside) {
                            onDismissRequest()
                        }
                    }
            )
        }
        InnerDialog(
            visible = visible,
            animationDuration = animationDuration,
            cancelable = cancelable,
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
}

@Composable
private fun BoxScope.InnerDialog(
    visible: Boolean,
    animationDuration: Int,
    cancelable: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetY by remember {
        mutableStateOf(value = 0f)
    }
    val offsetYAnimate by animateFloatAsState(
        targetValue = offsetY
    )
    var bottomSheetHeight by remember {
        mutableStateOf(value = 0f)
    }
    AnimatedVisibility(
        modifier = Modifier
            .clickableNoRipple {

            }
            .align(alignment = Alignment.BottomCenter)
            .onGloballyPositioned {
                bottomSheetHeight = it.size.height.toFloat()
            }
            .offset(
                offset = {
                    IntOffset(0, offsetYAnimate.roundToInt())
                }
            )
            .draggable(
                state = rememberDraggableState(
                    onDelta = {
                        offsetY = (offsetY + it.toInt()).coerceAtLeast(0f)
                    }
                ),
                orientation = Orientation.Vertical,
                onDragStarted = {

                },
                onDragStopped = {
                    if (cancelable && offsetY > bottomSheetHeight / 2) {
                        onDismissRequest()
                    } else {
                        offsetY = 0f
                    }
                }),
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = LinearOutSlowInEasing
            ),
            initialOffsetY = { 2 * it }),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = LinearOutSlowInEasing
            ),
            targetOffsetY = { it }
        ),
    ) {
        DisposableEffect(key1 = null) {
            onDispose {
                offsetY = 0f
            }
        }
        content()
    }
}