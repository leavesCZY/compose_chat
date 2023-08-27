package github.leavesczy.compose_chat.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object FileUtils {

    suspend fun getMimeType(filePath: String): String? {
        return withContext(context = Dispatchers.IO) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            return@withContext options.outMimeType
        }
    }

    fun getExtensionFromMimeType(mimeType: String): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    suspend fun createTempFile(context: Context, extension: String): File {
        return withContext(context = Dispatchers.IO) {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, createFileName(extension = extension))
            file.createNewFile()
            return@withContext file
        }
    }

    fun createFileName(extension: String): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault())
        val time = simpleDateFormat.format(date)
        return "compose_chat_$time.$extension"
    }

    suspend fun copyExifOrientation(
        sourceImagePath: String,
        targetImagePath: String
    ) {
        withContext(context = Dispatchers.IO) {
            val sourceExifInterface = ExifInterface(sourceImagePath)
            val sourceExifOrientation = sourceExifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val targetExifInterface = ExifInterface(targetImagePath)
            targetExifInterface.setAttribute(
                ExifInterface.TAG_ORIENTATION,
                sourceExifOrientation.toString()
            )
            targetExifInterface.saveAttributes()
        }
    }

}