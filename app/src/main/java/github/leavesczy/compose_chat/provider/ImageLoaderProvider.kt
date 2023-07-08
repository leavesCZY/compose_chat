package github.leavesczy.compose_chat.provider

import android.app.Application
import android.content.Context
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ImageLoaderProvider {

    fun init(application: Application) {
        val imageLoader = ImageLoader
            .Builder(context = application)
            .crossfade(enable = false)
            .allowHardware(enable = true)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)
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

    @OptIn(ExperimentalCoilApi::class)
    private fun getCachedFile(context: Context, imageUrl: String): File? {
        val snapshot = context.imageLoader.diskCache?.openSnapshot(imageUrl)
        val file = snapshot?.data?.toFile()
        snapshot?.close()
        if (file != null && file.exists()) {
            return file
        }
        return null
    }

}