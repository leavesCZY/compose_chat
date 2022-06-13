package github.leavesczy.compose_chat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import github.leavesczy.matisse.MediaResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * @Author: leavesCZY
 * @Date: 2022/6/13 16:06
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object CompressImageUtils {

    private const val IMAGE_MAX_SIZE = (0.8 * 1024 * 1024).toInt()

    private const val GIF_MIME = "image/gif"

    private const val JPEG = "jpeg"

    suspend fun compressImage(context: Context, mediaResources: MediaResources): File? {
        return withContext(context = Dispatchers.IO) {
            try {
                val imageUri = mediaResources.uri
                val imagePath = mediaResources.path
                val extension = imagePath.substringAfterLast('.', "")
                val isGif = mediaResources.mimeType == GIF_MIME
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream != null) {
                    val byteArray = inputStream.readBytes()
                    if (isGif || byteArray.size <= IMAGE_MAX_SIZE) {
                        if (isAndroidQ()) {
                            val imageTempFile =
                                createImageTempFile(context = context, extension = extension)
                            val imageTempFileOutputStream = FileOutputStream(imageTempFile)
                            imageTempFileOutputStream.write(byteArray)
                            imageTempFileOutputStream.close()
                            return@withContext imageTempFile
                        }
                        return@withContext File(imagePath)
                    }
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    val imageTempFile = createImageTempFile(context = context, extension = JPEG)
                    compressImage(
                        bitmap = bitmap,
                        maxSize = IMAGE_MAX_SIZE,
                        targetFile = imageTempFile
                    )
                    return@withContext imageTempFile
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private fun compressImage(bitmap: Bitmap, maxSize: Int, targetFile: File) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100
        while (true) {
            byteArrayOutputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            if (byteArrayOutputStream.size() > maxSize) {
                quality -= 10
            } else {
                FileOutputStream(targetFile).apply {
                    write(byteArrayOutputStream.toByteArray())
                    close()
                }
                return
            }
        }
    }

    private fun createImageTempFile(context: Context, extension: String): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            UUID.randomUUID().toString(),
            extension,
            storageDir
        )
    }

    private fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

}