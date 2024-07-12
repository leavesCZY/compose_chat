package github.leavesczy.compose_chat.extend

import android.os.SystemClock

/**
 * @Author: leavesCZY
 * @Github: https://github.com/leavesCZY
 * @Desc:
 */
class ComposeOnClick(private val onClick: () -> Unit) : Function0<Unit> {

    companion object {

        private var lastClickTime = 0L

    }

    override fun invoke() {
        val currentTime = SystemClock.elapsedRealtime()
        val isEnabled = currentTime - lastClickTime > 250L
        if (isEnabled) {
            lastClickTime = currentTime
            onClick()
        }
    }

}