package github.leavesczy.compose_chat.utils

import android.content.Context
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.imageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
object ImageUtils {

    fun init() {
        SingletonImageLoader.setSafe(factory = { context ->
            ImageLoader
                .Builder(context = context)
                .crossfade(enable = false)
                .components {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        add(factory = AnimatedImageDecoder.Factory())
                    } else {
                        add(factory = GifDecoder.Factory())
                    }
                    add(
                        OkHttpNetworkFetcherFactory(
                            callFactory = {
                                val timeoutSecond = 15L
                                OkHttpClient.Builder()
                                    .connectTimeout(
                                        timeout = timeoutSecond,
                                        unit = TimeUnit.SECONDS
                                    )
                                    .readTimeout(timeout = timeoutSecond, unit = TimeUnit.SECONDS)
                                    .writeTimeout(timeout = timeoutSecond, unit = TimeUnit.SECONDS)
                                    .retryOnConnectionFailure(retryOnConnectionFailure = true)
                                    .build()
                            }
                        )
                    )
                }
                .build()
        })
    }

    suspend fun getCachedFileOrDownload(context: Context, imageUrl: String): File? {
        return withContext(context = Dispatchers.IO) {
            val file = getCachedFile(context = context, imageUrl = imageUrl)
            if (file != null) {
                return@withContext file
            }
            val request = ImageRequest.Builder(context = context).data(data = imageUrl).build()
            val imageResult = context.imageLoader.execute(request = request)
            if (imageResult is SuccessResult) {
                return@withContext getCachedFile(context = context, imageUrl = imageUrl)
            }
            return@withContext null
        }
    }

    private suspend fun getCachedFile(context: Context, imageUrl: String): File? {
        return withContext(context = Dispatchers.IO) {
            val snapshot = context.imageLoader.diskCache?.openSnapshot(imageUrl)
            val file = snapshot?.data?.toFile()
            snapshot?.close()
            if (file != null && file.exists()) {
                return@withContext file
            }
            return@withContext null
        }
    }

}