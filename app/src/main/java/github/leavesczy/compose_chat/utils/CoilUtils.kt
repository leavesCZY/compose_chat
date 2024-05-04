package github.leavesczy.compose_chat.utils

import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2024/4/1 15:55
 * @Desc:
 */
object CoilUtils {

    @OptIn(DelicateCoilApi::class)
    fun init(application: Context) {
        val imageLoader = ImageLoader
            .Builder(context = application)
            .crossfade(enable = false)
            .allowHardware(enable = true)
            .build()
        SingletonImageLoader.setUnsafe(imageLoader = imageLoader)
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