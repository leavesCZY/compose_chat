package github.leavesczy.compose_chat.extend

import android.os.SystemClock

/**
 * @Author: leavesCZY
 * @Github: https://github.com/leavesCZY
 * @Desc:
 */
class ComposeOnClick(private val onClick: () -> Unit) : Function0<Unit> {

    companion object {

        private const val MIN_DURATION = 300L

        private var lastClickTime = 0L

    }

    override fun invoke() {
        val currentTime = SystemClock.elapsedRealtime()
        val isEnabled = currentTime - lastClickTime > MIN_DURATION
        if (isEnabled) {
            lastClickTime = currentTime
            onClick()
        }
    }

}