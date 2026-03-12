package github.leavesczy.compose_chat.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object AlbumUtils {

    private const val ALBUM_DIRECTORY_NAME = "compose_chat"

    private const val JPEG = "jpeg"

    private const val JPEG_MIME_TYPE = "image/jpeg"

    suspend fun insertImageToAlbum(context: Context, imageUri: String): Boolean {
        return withContext(context = Dispatchers.IO) {
            try {
                val isHttpUrl = imageUri.startsWith(prefix = "http")
                val imageFile = if (isHttpUrl) {
                    ImageUtils.getCachedFileOrDownload(
                        context = context,
                        imageUrl = imageUri
                    )
                } else {
                    File(imageUri)
                }
                if (imageFile != null) {
                    return@withContext insertImageToAlbum(context = context, imageFile = imageFile)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    private suspend fun insertImageToAlbum(context: Context, imageFile: File): Boolean {
        return withContext(context = Dispatchers.IO) {
            val mimeType =
                FileUtils.getMimeType(filePath = imageFile.absolutePath) ?: JPEG_MIME_TYPE
            val extension = FileUtils.getExtensionFromMimeType(mimeType = mimeType) ?: JPEG
            val albumImageOutputStream = generateAlbumImageOutputStream(
                context = context,
                mimeType = mimeType,
                displayName = FileUtils.createFileName(),
                extension = extension
            )
            if (albumImageOutputStream != null) {
                val fileInputStream = FileInputStream(imageFile)
                fileInputStream.copyTo(albumImageOutputStream)
                albumImageOutputStream.close()
                fileInputStream.close()
                return@withContext true
            }
            return@withContext false
        }
    }

    private suspend fun generateAlbumImageOutputStream(
        context: Context,
        mimeType: String,
        displayName: String,
        extension: String
    ): OutputStream? {
        return withContext(context = Dispatchers.IO) {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "${displayName}.${extension}")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + File.separator + ALBUM_DIRECTORY_NAME
                )
            } else {
                val directory = File(
                    Environment.getExternalStorageDirectory().path +
                            File.separator +
                            Environment.DIRECTORY_DCIM +
                            File.separator +
                            ALBUM_DIRECTORY_NAME
                )
                directory.mkdirs()
                val imageFile = File.createTempFile(displayName, ".${extension}", directory)
                contentValues.put(MediaStore.MediaColumns.DATA, imageFile.absolutePath)
            }
            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (uri != null) {
                return@withContext contentResolver.openOutputStream(uri)
            }
            return@withContext null
        }
    }

}