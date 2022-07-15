package github.leavesczy.compose_chat.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import github.leavesczy.compose_chat.ui.widgets.CoilImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.util.*

/**
 * @Author: CZY
 * @Date: 2022/6/13 11:59
 * @Desc:
 */
object AlbumUtils {

    private const val ALBUM_DIRECTORY = "compose_chat"

    private const val JPEG_MIME = "image/jpeg"

    suspend fun insertImageToAlbum(context: Context, imageUrl: String): Boolean {
        return withContext(context = Dispatchers.IO) {
            try {
                val isHttpUrl = imageUrl.startsWith(prefix = "http")
                val imageFile = if (isHttpUrl) {
                    CoilImageLoader.getCachedFileOrDownload(
                        context = context,
                        imageUrl = imageUrl
                    )
                } else {
                    File(imageUrl)
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

    private fun insertImageToAlbum(context: Context, imageFile: File): Boolean {
        val displayName = UUID.randomUUID().toString()
        var mimeType = getMimeType(imageFile.absolutePath)
        if (mimeType.isNullOrBlank()) {
            mimeType = JPEG_MIME
        }
        val albumImageOutputStream = generateAlbumImageOutputStream(
            context = context,
            mimeType = mimeType,
            displayName = displayName
        )
        if (albumImageOutputStream != null) {
            val fileInputStream = FileInputStream(imageFile)
            fileInputStream.copyTo(albumImageOutputStream)
            albumImageOutputStream.close()
            fileInputStream.close()
            return true
        }
        return false
    }

    private fun generateAlbumImageOutputStream(
        context: Context,
        displayName: String,
        mimeType: String
    ): OutputStream? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DCIM + File.separator + ALBUM_DIRECTORY
            )
        } else {
//            contentValues.put(
//                MediaStore.MediaColumns.DATA,
//                Environment.getExternalStorageDirectory().path
//                        + File.separator + Environment.DIRECTORY_DCIM
//                        + File.separator + ALBUM_DIRECTORY
//                        + File.separator + displayName
//            )
        }
        val contentResolver = context.contentResolver
        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            return contentResolver.openOutputStream(uri)
        }
        return null
    }

    private fun getMimeType(filePath: String): String? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        return options.outMimeType
    }

}