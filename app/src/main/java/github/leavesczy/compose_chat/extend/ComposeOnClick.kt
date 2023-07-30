package github.leavesczy.compose_chat.extend

import android.os.SystemClock
import android.util.Log

/**
 * @Author: leavesCZY
 * @Github: https://github.com/leavesCZY
 * @Desc:
 */
class ComposeOnClick(private val onClick: () -> Unit) : Function0<Unit> {

    companion object {

        private const val MIN_DURATION = 500L

        private var lastClickTime = 0L

    }

    override fun invoke() {
        val currentTime = SystemClock.elapsedRealtime()
        val isEnabled = currentTime - lastClickTime > MIN_DURATION
        log("onClick isEnabled : $isEnabled")
        if (isEnabled) {
            lastClickTime = currentTime
            onClick()
        }
    }

    private fun log(log: String) {
        Log.e(
            javaClass.simpleName,
            "${System.identityHashCode(this)} ${System.identityHashCode(onClick)} $log"
        )
    }

}