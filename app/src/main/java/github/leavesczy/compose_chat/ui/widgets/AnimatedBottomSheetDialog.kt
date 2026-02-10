package github.leavesczy.compose_chat.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import kotlin.math.roundToInt

/**
 * @Author: leavesCZY
 * @Date: 2025/9/5 15:33
 * @Desc:
 */
@Composable
fun AnimatedBottomSheetDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    cancelable: Boolean = true,
    shape: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    backgroundColor: Color = ComposeChatTheme.colorScheme.c_FFEFF1F3_FF22202A.color,
    navigationBarsColor: Color = backgroundColor,
    shadowElevation: Dp = 6.dp,
    dragHandle: (@Composable () -> Unit)? = {
        DragHandle()
    },
    backgroundMask: (@Composable AnimatedVisibilityScope.() -> Unit)? = {
        BackgroundMask(
            modifier = Modifier
                .animateEnterExit(
                    enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 200))
                ),
            cancelable = cancelable,
            onDismissRequest = onDismissRequest
        )
    },
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        BackHandler(
            enabled = visible,
            onBack = {
                if (cancelable) {
                    onDismissRequest()
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (backgroundMask != null) {
                backgroundMask()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    BottomSheetDialog(
                        modifier = Modifier
                            .animateEnterExit(
                                enter = slideInVertically(
                                    animationSpec = tween(durationMillis = 350),
                                    initialOffsetY = { it }
                                ),
                                exit = slideOutVertically(
                                    animationSpec = tween(durationMillis = 350),
                                    targetOffsetY = { it }
                                )
                            ),
                        shape = shape,
                        backgroundColor = backgroundColor,
                        shadowElevation = shadowElevation,
                        cancelable = cancelable,
                        dragHandle = dragHandle,
                        onDismissRequest = onDismissRequest,
                        content = content
                    )
                }
                Spacer(
                    modifier = Modifier
                        .animateEnterExit(
                            enter = slideInVertically(
                                animationSpec = tween(durationMillis = 350),
                                initialOffsetY = { it }
                            ),
                            exit = slideOutVertically(
                                animationSpec = tween(durationMillis = 380),
                                targetOffsetY = { it }
                            )
                        )
                        .background(color = navigationBarsColor)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
private fun BottomSheetDialog(
    modifier: Modifier,
    shape: Shape,
    backgroundColor: Color,
    shadowElevation: Dp,
    cancelable: Boolean,
    onDismissRequest: () -> Unit,
    dragHandle: @Composable (() -> Unit)?,
    content: @Composable () -> Unit
) {
    var offsetY by remember {
        mutableFloatStateOf(value = 0f)
    }
    val offsetYAnimate by animateFloatAsState(
        targetValue = offsetY
    )
    var bottomSheetHeight by remember {
        mutableFloatStateOf(value = 0f)
    }
    Column(
        modifier = modifier
            .onGloballyPositioned {
                bottomSheetHeight = it.size.height.toFloat()
            }
            .offset(offset = {
                IntOffset(x = 0, y = offsetYAnimate.roundToInt())
            })
            .draggable(
                state = rememberDraggableState(
                    onDelta = {
                        offsetY = (offsetY + it.toInt()).coerceAtLeast(minimumValue = 0f)
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
                }
            )
            .fillMaxWidth()
            .then(
                other = if (shadowElevation == 0.dp) {
                    Modifier
                } else {
                    Modifier
                        .shadow(elevation = shadowElevation, shape = shape)
                }
            )
            .clip(shape = shape)
            .then(
                other = if (backgroundColor == Color.Transparent) {
                    Modifier
                } else {
                    Modifier
                        .background(color = backgroundColor)
                }
            )
            .clickableNoRipple(onClick = {}),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (dragHandle != null) {
            dragHandle()
        }
        content()
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 36.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(width = 60.dp, height = 4.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp))
                .background(color = ComposeChatTheme.colorScheme.c_333A3D4D_B3FFFFFF.color)
        )
    }
}

@Composable
private fun BackgroundMask(
    modifier: Modifier,
    cancelable: Boolean,
    onDismissRequest: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ComposeChatTheme.colorScheme.c_80000000_99000000.color)
            .clickableNoRipple(
                onClick = if (cancelable) {
                    onDismissRequest
                } else {
                    {}
                }
            )
    )
}