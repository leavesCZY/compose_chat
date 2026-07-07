package github.leavesczy.compose_chat.extensions

import android.os.SystemClock

class ComposeOnClick(private val onClick: () -> Unit) : Function0<Unit> {

    companion object {

        private var lastClickTime = 0L

    }

    override fun invoke() {
        val currentTime = SystemClock.elapsedRealtime()
        val isEnabled = currentTime - lastClickTime > 150L
        if (isEnabled) {
            lastClickTime = currentTime
            onClick()
        }
    }

}