package github.leavesczy.compose_chat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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

    private const val IMAGE_MAX_SIZE = (1.0 * 1024 * 1024).toInt()

    private const val JPEG_MIME_TYPE = "image/jpeg"

    private const val GIF_MIME_TYPE = "image/gif"

    private const val WEBP_MIME_TYPE = "image/webp"

    private const val JPEG = "jpeg"

    suspend fun compressImage(context: Context, imageUri: Uri): File? {
        return withContext(context = Dispatchers.IO) {
            try {
                val openFileDescriptor = context.contentResolver.openFileDescriptor(imageUri, "r")
                    ?: return@withContext null
                val fileDescriptor = openFileDescriptor.fileDescriptor
                val fileSize = openFileDescriptor.statSize
                val mustBeCompressed = if (fileSize <= IMAGE_MAX_SIZE) {
                    false
                } else {
                    val imageMimeType =
                        FileUtils.getMimeType(context = context, uri = imageUri) ?: JPEG_MIME_TYPE
                    val isAnimatedImage =
                        imageMimeType == GIF_MIME_TYPE || imageMimeType == WEBP_MIME_TYPE
                    !isAnimatedImage
                }
                val result: File?
                if (mustBeCompressed) {
                    val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    val tempFile = FileUtils.createTempFile(context = context, extension = JPEG)
                    compressImage(
                        bitmap = bitmap,
                        maxSize = IMAGE_MAX_SIZE,
                        targetFile = tempFile
                    )
                    FileUtils.copyExifOrientation(
                        sourceImageFileDescriptor = fileDescriptor,
                        targetImagePath = tempFile.absolutePath
                    )
                    result = tempFile
                } else {
                    result = if (isAndroidQ()) {
                        val imageMimeType =
                            FileUtils.getMimeType(context = context, uri = imageUri)
                                ?: JPEG_MIME_TYPE
                        val imageExtension =
                            FileUtils.getExtensionFromMimeType(mimeType = imageMimeType) ?: JPEG
                        val tempFile = FileUtils.createTempFile(
                            context = context,
                            extension = imageExtension
                        )
                        val inputStream = context.contentResolver.openInputStream(imageUri)
                        val byteArray = inputStream?.readBytes()
                        inputStream?.close()
                        if (byteArray == null) {
                            null
                        } else {
                            tempFile.writeBytes(byteArray)
                            FileUtils.copyExifOrientation(
                                sourceImageFileDescriptor = fileDescriptor,
                                targetImagePath = tempFile.absolutePath
                            )
                            tempFile
                        }
                    } else {
                        val filePath =
                            FileUtils.getFilePathFromUri(context = context, uri = imageUri)
                        if (filePath.isNullOrBlank()) {
                            null
                        } else {
                            File(filePath)
                        }
                    }
                }
                openFileDescriptor.close()
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