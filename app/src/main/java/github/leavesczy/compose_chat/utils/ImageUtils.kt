package github.leavesczy.compose_chat.utils

import android.content.Context
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.imageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

object ImageUtils {

    private val downloadClient by lazy {
        val timeoutSecond = 15L
        OkHttpClient.Builder()
            .connectTimeout(timeout = timeoutSecond, unit = TimeUnit.SECONDS)
            .readTimeout(timeout = timeoutSecond, unit = TimeUnit.SECONDS)
            .writeTimeout(timeout = timeoutSecond, unit = TimeUnit.SECONDS)
            .retryOnConnectionFailure(retryOnConnectionFailure = true)
            .build()
    }

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
                                downloadClient
                            }
                        )
                    )
                }
                .build()
        })
    }

    suspend fun getCachedFileOrDownload(context: Context, imageUrl: String): File? {
        return withContext(context = Dispatchers.IO) {
            val cached = getCachedFile(context = context, imageUrl = imageUrl)
            if (cached != null) {
                return@withContext cached
            }
            val request = ImageRequest.Builder(context = context)
                .data(data = imageUrl)
                .diskCachePolicy(policy = CachePolicy.ENABLED)
                .memoryCachePolicy(policy = CachePolicy.ENABLED)
                .build()
            val imageResult = context.imageLoader.execute(request = request)
            if (imageResult is SuccessResult) {
                val diskFile = getCachedFile(context = context, imageUrl = imageUrl)
                if (diskFile != null) {
                    return@withContext diskFile
                }
            }
            downloadToTempFile(context = context, imageUrl = imageUrl)
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

    private suspend fun downloadToTempFile(context: Context, imageUrl: String): File? {
        return withContext(context = Dispatchers.IO) {
            try {
                val response = downloadClient.newCall(
                    request = Request.Builder().url(imageUrl).get().build()
                ).execute()
                if (!response.isSuccessful) {
                    response.close()
                    return@withContext null
                }
                val body = response.body
                val mimeType = body.contentType()?.toString() ?: "image/jpeg"
                val extension = FileUtils.getExtensionFromMimeType(mimeType = mimeType) ?: "jpg"
                val tempFile = FileUtils.createTempFile(context = context, extension = extension)
                body.byteStream().use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                response.close()
                tempFile
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }
    }

}
