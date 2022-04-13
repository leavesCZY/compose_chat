package github.leavesczy.compose_chat.utils

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2022/1/1 13:10
 * @Desc:
 */
object ImageUtils {

    private const val MAX_BITMAP_SIZE = 1.5 * 1024 * 1024

    private val Context.imageOutputDir: File
        get() = externalCacheDir ?: cacheDir

    private val randomFileName: String
        get() = (System.currentTimeMillis() + Random.nextInt(2022, 6000)).toString()

    fun saveImageToCacheDir(context: Context, imageUri: Uri): File? {
        try {
            val options = BitmapFactory.Options()
            val imageInputStream = context.contentResolver?.openInputStream(imageUri) ?: return null
            val decodeBitmap =
                BitmapFactory.decodeStream(imageInputStream, null, options) ?: return null
            val compressedImageOutputStream = compressImage(decodeBitmap)
            val imageTempFile = createImageFile(context = context)
            val imageTempFileOutputStream = FileOutputStream(imageTempFile)
            val bitmapInputStream =
                ByteArrayInputStream(compressedImageOutputStream.toByteArray())
            bitmapInputStream.copyTo(out = imageTempFileOutputStream)
            imageInputStream.close()
            imageTempFileOutputStream.close()
            compressedImageOutputStream.close()
            bitmapInputStream.close()
            return imageTempFile
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("TAg", e.message ?: "")
        }
        return null
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

    private fun createImageFile(context: Context): File {
        val imageFile = File(context.imageOutputDir, "$randomFileName.jpg")
        if (imageFile.exists()) {
            imageFile.delete()
        }
        imageFile.createNewFile()
        return imageFile
    }

    suspend fun insertImageToAlbum(
        context: Context,
        data: Any
    ): Boolean {
        return withContext(context = Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(data = data)
                    .build()
                val bitmap = context.imageLoader.execute(request).drawable?.toBitmap()
                if (bitmap != null) {
                    return@withContext insertImageToAlbum(context = context, bitmap = bitmap)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    private fun generateImageName(): String {
        return "compose_chat_" + UUID.randomUUID().toString() + ".jpg"
    }

    private fun generateImageMimeType(): String {
        return "image/jpeg"
    }

    private fun insertImageToAlbum(
        context: Context,
        bitmap: Bitmap
    ): Boolean {
        try {
            val displayName = generateImageName()
            val mimeType = generateImageMimeType()
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val inputStream = ByteArrayInputStream(byteArray)
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            } else {
                contentValues.put(
                    MediaStore.MediaColumns.DATA,
                    Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DCIM + File.separator + displayName
                )
            }
            val contentResolver = context.contentResolver
            val uri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    inputStream.copyTo(out = outputStream)
                    outputStream.close()
                    inputStream.close()
                    return true
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

}