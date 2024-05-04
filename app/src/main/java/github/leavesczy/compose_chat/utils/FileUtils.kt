package github.leavesczy.compose_chat.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileDescriptor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object FileUtils {

    suspend fun getFilePathFromUri(context: Context, uri: Uri): String? {
        return withContext(context = Dispatchers.Default) {
            val filePath = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> {
                    var filePath: String? = null
                    val dataColumn = MediaStore.MediaColumns.DATA
                    val cursor =
                        context.contentResolver.query(uri, arrayOf(dataColumn), null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(dataColumn)
                        filePath = cursor.getString(columnIndex)
                    }
                    cursor?.close()
                    filePath
                }

                ContentResolver.SCHEME_FILE -> {
                    uri.path
                }

                else -> {
                    null
                }
            }
            return@withContext if (filePath.isNullOrBlank()) {
                null
            } else {
                filePath
            }
        }
    }

    suspend fun getMimeType(filePath: String): String? {
        return withContext(context = Dispatchers.IO) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            val outMimeType = options.outMimeType
            return@withContext if (outMimeType.isNullOrBlank()) {
                null
            } else {
                outMimeType
            }
        }
    }

    suspend fun getMimeType(context: Context, uri: Uri): String? {
        return withContext(context = Dispatchers.Default) {
            val mimeType = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> {
                    context.contentResolver.getType(uri)
                }

                ContentResolver.SCHEME_FILE -> {
                    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                        uri.toString()
                    )
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.lowercase(Locale.getDefault())
                    )
                }

                else -> {
                    null
                }
            }
            return@withContext if (mimeType.isNullOrBlank()) {
                null
            } else {
                mimeType
            }
        }
    }

    suspend fun getExtensionFromMimeType(mimeType: String): String? {
        return withContext(context = Dispatchers.Default) {
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            return@withContext if (extension.isNullOrBlank()) {
                null
            } else {
                extension
            }
        }
    }

    suspend fun createTempFile(context: Context, extension: String): File {
        return withContext(context = Dispatchers.IO) {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, createFileName(extension = extension))
            file.createNewFile()
            return@withContext file
        }
    }

    private fun createFileName(extension: String): String {
        return "${createFileName()}.$extension"
    }

    fun createFileName(): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault())
        val time = simpleDateFormat.format(date)
        return "compose_chat_$time"
    }

    suspend fun copyExifOrientation(
        sourceImageFileDescriptor: FileDescriptor,
        targetImagePath: String
    ) {
        withContext(context = Dispatchers.IO) {
            val sourceExifInterface = ExifInterface(sourceImageFileDescriptor)
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