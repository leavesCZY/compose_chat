package github.leavesczy.compose_chat.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import github.leavesczy.compose_chat.ui.widgets.CoilImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 13:10
 * @Desc:
 */
object ImageUtils {

    private const val MAX_BITMAP_SIZE = 1.5 * 1024 * 1024

    private const val GIF_MIME = "image/gif"

    private const val GIF = "gif"

    private const val JPEG = "jpeg"

    private val Context.imageOutputDir: File
        get() = externalCacheDir ?: cacheDir

    private val randomFileName: String
        get() = (System.currentTimeMillis() + Random.nextInt(2022, 6000)).toString()

    fun saveImageToCacheDir(context: Context, imageUri: Uri): File? {
        try {
            val imageInputStream = context.contentResolver?.openInputStream(imageUri) ?: return null
            val isGif = isGif(context = context, imageUri = imageUri)
            val mimeType = if (isGif) {
                GIF
            } else {
                JPEG
            }
            val imageTempFile = createImageFile(context = context, mimeType = mimeType)
            val imageTempFileOutputStream = FileOutputStream(imageTempFile)
            if (isGif) {
                imageInputStream.copyTo(out = imageTempFileOutputStream)
            } else {
                val decodeBitmap = BitmapFactory.decodeStream(
                    imageInputStream,
                    null,
                    null
                ) ?: return null
                val bitmapByteArray = compressImage(image = decodeBitmap).toByteArray()
                imageTempFileOutputStream.write(bitmapByteArray)
            }
            imageInputStream.close()
            imageTempFileOutputStream.close()
            return imageTempFile
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    suspend fun insertImageToAlbum(
        context: Context,
        imageUrl: String
    ): Boolean {
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

    private fun insertImageToAlbum(
        context: Context,
        bitmap: Bitmap
    ): Boolean {
        try {
            val albumImageOutputStream =
                generateAlbumImageOutputStream(context = context, imageType = JPEG)
            if (albumImageOutputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, albumImageOutputStream)
                albumImageOutputStream.close()
                return true
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    private fun insertImageToAlbum(context: Context, imageFile: File): Boolean {
        try {
            val isGif = isGif(imagePath = imageFile.absolutePath)
            val imageType = if (isGif) {
                GIF
            } else {
                JPEG
            }
            val albumImageOutputStream =
                generateAlbumImageOutputStream(context = context, imageType = imageType)
            if (albumImageOutputStream != null) {
                val fileInputStream = FileInputStream(imageFile)
                fileInputStream.copyTo(albumImageOutputStream)
                albumImageOutputStream.close()
                fileInputStream.close()
                return true
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    private fun generateAlbumImageOutputStream(
        context: Context,
        imageType: String,
        imageDisplayName: String = generateImageName(mimeType = imageType)
    ): OutputStream? {
        try {
            val imageMimeType = "image/$imageType"
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageDisplayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, imageMimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            } else {
                contentValues.put(
                    MediaStore.MediaColumns.DATA,
                    Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DCIM + File.separator + imageDisplayName
                )
            }
            val contentResolver = context.contentResolver
            val uri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                return contentResolver.openOutputStream(uri)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    private fun isGif(context: Context, imageUri: Uri): Boolean {
        val imageInputStream = context.contentResolver?.openInputStream(imageUri) ?: return false
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(imageInputStream, null, options)
        return options.outMimeType == GIF_MIME
    }

    private fun isGif(imagePath: String): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        return options.outMimeType == GIF_MIME
    }

    private fun compressImage(image: Bitmap): ByteArrayOutputStream {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        var options = 100
        while (byteArrayOutputStream.toByteArray().size > MAX_BITMAP_SIZE) {
            byteArrayOutputStream.reset()
            image.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream)
            options -= 10
        }
        return byteArrayOutputStream
    }

    private fun generateImageName(mimeType: String): String {
        return "compose_chat_" + UUID.randomUUID().toString() + "." + mimeType
    }

    private fun createImageFile(context: Context, mimeType: String): File {
        val imageFile = File(context.imageOutputDir, "$randomFileName.$mimeType")
        if (imageFile.exists()) {
            imageFile.delete()
        }
        imageFile.createNewFile()
        return imageFile
    }

}