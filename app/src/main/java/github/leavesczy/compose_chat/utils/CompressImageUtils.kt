package github.leavesczy.compose_chat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object CompressImageUtils {

    private const val IMAGE_MAX_SIZE = (0.8 * 1024 * 1024).toInt()

    private const val GIF_MIME = "image/gif"

    private const val JPEG = "jpeg"

    @Suppress("SameParameterValue")
    suspend fun compressImage(
        context: Context, mediaResource: MediaResource
    ): File? {
        return withContext(context = Dispatchers.IO) {
            try {
                val imageUri = mediaResource.uri
                val imagePath = mediaResource.path
                val extension = imagePath.substringAfterLast(
                    '.', ""
                )
                val isGif = mediaResource.mimeType == GIF_MIME
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream != null) {
                    val byteArray = inputStream.readBytes()
                    inputStream.close()
                    if (isGif || byteArray.size <= IMAGE_MAX_SIZE) {
                        if (isAndroidQ()) {
                            val imageTempFile = createTempFile(
                                context = context, extension = extension
                            )
                            val imageTempFileOutputStream = FileOutputStream(imageTempFile)
                            imageTempFileOutputStream.write(byteArray)
                            imageTempFileOutputStream.close()
                            return@withContext imageTempFile
                        }
                        return@withContext File(imagePath)
                    }
                    val bitmap = BitmapFactory.decodeByteArray(
                        byteArray, 0, byteArray.size
                    )
                    val imageTempFile = createTempFile(
                        context = context, extension = JPEG
                    )
                    compressImage(
                        bitmap = bitmap, maxSize = IMAGE_MAX_SIZE, targetFile = imageTempFile
                    )
                    return@withContext imageTempFile
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    @Suppress("SameParameterValue")
    private fun compressImage(
        bitmap: Bitmap, maxSize: Int, targetFile: File
    ) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100
        while (true) {
            byteArrayOutputStream.reset()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream
            )
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

    private fun createTempFile(
        context: Context, extension: String
    ): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(
            storageDir, createImageName(extension = extension)
        )
        file.createNewFile()
        return file
    }

    private fun createImageName(extension: String): String {
        val uuid = UUID.randomUUID().toString()
        val randomName = uuid.substring(
            0, 6
        )
        return "compose_chat_$randomName.$extension"
    }

    private fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

}