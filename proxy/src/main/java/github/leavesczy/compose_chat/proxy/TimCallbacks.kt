package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.models.ActionResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal suspend fun suspendTimCallback(block: (V2TIMCallback) -> Unit): ActionResult {
    return suspendCancellableCoroutine { continuation ->
        block(
            object : V2TIMCallback {
                override fun onSuccess() {
                    if (continuation.isActive) {
                        continuation.resume(value = ActionResult.Success)
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    if (continuation.isActive) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc
                            )
                        )
                    }
                }
            }
        )
    }
}

internal suspend fun <T> suspendTimValue(
    onError: (code: Int, desc: String?) -> T,
    block: (V2TIMValueCallback<T>) -> Unit
): T {
    return suspendCancellableCoroutine { continuation ->
        block(
            object : V2TIMValueCallback<T> {
                override fun onSuccess(t: T) {
                    if (continuation.isActive) {
                        continuation.resume(value = t)
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    if (continuation.isActive) {
                        continuation.resume(value = onError(code, desc))
                    }
                }
            }
        )
    }
}

internal suspend fun <T : Any> suspendTimValueOrNull(
    block: (V2TIMValueCallback<T>) -> Unit
): T? {
    return suspendCancellableCoroutine { continuation ->
        block(
            object : V2TIMValueCallback<T> {
                override fun onSuccess(t: T) {
                    if (continuation.isActive) {
                        continuation.resume(value = t)
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    if (continuation.isActive) {
                        continuation.resume(value = null)
                    }
                }
            }
        )
    }
}

internal suspend fun <T : Any> suspendTimValueResult(
    block: (V2TIMValueCallback<T>) -> Unit
): ActionResult {
    return suspendCancellableCoroutine { continuation ->
        block(
            object : V2TIMValueCallback<T> {
                override fun onSuccess(t: T) {
                    if (continuation.isActive) {
                        continuation.resume(value = ActionResult.Success)
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    if (continuation.isActive) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc
                            )
                        )
                    }
                }
            }
        )
    }
}
