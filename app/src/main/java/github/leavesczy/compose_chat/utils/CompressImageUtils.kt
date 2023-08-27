package github.leavesczy.compose_chat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object CompressImageUtils {

    private const val IMAGE_MAX_SIZE = (0.8 * 1024 * 1024).toInt()

    private const val jpegMime = "image/jpeg"

    private const val webpMime = "image/webp"

    private const val gifMime = "image/gif"

    private const val jpeg = "jpeg"

    suspend fun compressImage(context: Context, mediaResource: MediaResource): File? {
        return withContext(context = Dispatchers.IO) {
            try {
                val imageUri = mediaResource.uri
                val imagePath = mediaResource.path
                val imageMimeType = FileUtils.getMimeType(filePath = imagePath) ?: jpegMime
                val imageExtension =
                    FileUtils.getExtensionFromMimeType(mimeType = imageMimeType) ?: jpeg
                val isAnimatedImage = imageMimeType == gifMime || imageMimeType == webpMime
                val result: File?
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    result = null
                } else {
                    val byteArray = inputStream.readBytes()
                    inputStream.close()
                    if (isAnimatedImage || byteArray.size <= IMAGE_MAX_SIZE) {
                        result = if (isAndroidQ()) {
                            val tempFile =
                                FileUtils.createTempFile(
                                    context = context,
                                    extension = imageExtension
                                )
                            tempFile.writeBytes(byteArray)
                            FileUtils.copyExifOrientation(
                                sourceImagePath = imagePath,
                                targetImagePath = tempFile.absolutePath
                            )
                            tempFile
                        } else {
                            File(imagePath)
                        }
                    } else {
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        val tempFile = FileUtils.createTempFile(context = context, extension = jpeg)
                        compressImage(
                            bitmap = bitmap,
                            maxSize = IMAGE_MAX_SIZE,
                            targetFile = tempFile
                        )
                        FileUtils.copyExifOrientation(
                            sourceImagePath = imagePath,
                            targetImagePath = tempFile.absolutePath
                        )
                        result = tempFile
                    }
                }
                return@withContext result
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private suspend fun compressImage(bitmap: Bitmap, maxSize: Int, targetFile: File) {
        withContext(context = Dispatchers.IO) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            var quality = 100
            while (true) {
                byteArrayOutputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                if (byteArrayOutputStream.size() > maxSize) {
                    quality -= 10
                } else {
                    targetFile.writeBytes(byteArrayOutputStream.toByteArray())
                    break
                }
            }
        }
    }

    private fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

}