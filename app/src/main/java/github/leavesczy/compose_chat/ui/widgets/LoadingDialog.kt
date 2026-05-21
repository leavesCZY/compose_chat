package github.leavesczy.compose_chat.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.theme.AppTheme

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class LoadingDialogViewState(
    val visible: MutableState<Boolean> = mutableStateOf(value = false),
    val cancelable: MutableState<Boolean> = mutableStateOf(value = false)
) {

    fun show(cancelable: Boolean = false) {
        this.cancelable.value = cancelable
        this.visible.value = true
    }

    fun dismiss() {
        this.visible.value = false
    }

}

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    viewState: LoadingDialogViewState
) {
    AnimatedVisibility(
        modifier = modifier
            .fillMaxSize(),
        visible = viewState.visible.value,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        BackHandler(
            enabled = !viewState.cancelable.value,
            onBack = {}
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clickableNoRipple(onClick = {})
        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .animateEnterExit(
                        enter = fadeIn(animationSpec = tween(delayMillis = 300)),
                        exit = ExitTransition.None
                    )
                    .size(size = 36.dp),
                trackColor = Color.Transparent,
                strokeWidth = 2.5.dp,
                color = AppTheme.colorScheme.c_FF1BA2E6_FF1BA2E6.color,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
            )
        }
    }
}