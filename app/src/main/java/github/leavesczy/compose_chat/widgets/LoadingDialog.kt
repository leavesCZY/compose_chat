package github.leavesczy.compose_chat.widgets

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
import github.leavesczy.compose_chat.extensions.clickableNoRipple
import github.leavesczy.compose_chat.theme.AppTheme

@Stable
data class LoadingDialogViewState(
    val isVisible: MutableState<Boolean> = mutableStateOf(value = false),
    val isCancelable: MutableState<Boolean> = mutableStateOf(value = false)
) {

    fun show(isCancelable: Boolean = false) {
        this.isCancelable.value = isCancelable
        this.isVisible.value = true
    }

    fun dismiss() {
        this.isVisible.value = false
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
        visible = viewState.isVisible.value,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        BackHandler(
            enabled = !viewState.isCancelable.value,
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
                color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
            )
        }
    }
}