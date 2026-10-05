package github.leavesczy.compose_chat.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Locale

object AlbumUtils {

    private const val ALBUM_DIRECTORY_NAME = "compose_chat"

    private const val FALLBACK_EXTENSION = "jpg"

    private const val FALLBACK_MIME_TYPE = "image/jpeg"

    suspend fun insertImageToAlbum(context: Context, imageUri: String): Boolean {
        return withContext(context = Dispatchers.IO) {
            try {
                when {
                    imageUri.startsWith(prefix = "http://") || imageUri.startsWith(prefix = "https://") -> {
                        val imageFile = ImageUtils.getCachedFileOrDownload(
                            context = context,
                            imageUrl = imageUri
                        ) ?: return@withContext false
                        insertImageToAlbum(
                            context = context,
                            imageFile = imageFile,
                            hintExtension = extensionFromPath(path = imageUri.toUri().path)
                        )
                    }

                    imageUri.startsWith(prefix = "content://") || imageUri.startsWith(prefix = "file://") -> {
                        insertImageToAlbum(context = context, imageUri = imageUri.toUri())
                    }

                    else -> {
                        val imageFile = File(imageUri)
                        if (!imageFile.exists()) {
                            return@withContext false
                        }
                        insertImageToAlbum(
                            context = context,
                            imageFile = imageFile,
                            hintExtension = extensionFromPath(path = imageFile.name)
                        )
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun insertImageToAlbum(
        context: Context,
        imageFile: File,
        hintExtension: String?
    ): Boolean {
        val imageMeta = resolveImageMeta(
            sniffedMimeType = FileUtils.getMimeType(filePath = imageFile.absolutePath),
            providerMimeType = null,
            hintExtension = hintExtension
        )
        return FileInputStream(imageFile).use { inputStream ->
            insertImageToAlbum(
                context = context,
                inputStream = inputStream,
                mimeType = imageMeta.mimeType,
                extension = imageMeta.extension
            )
        }
    }

    private suspend fun insertImageToAlbum(context: Context, imageUri: Uri): Boolean {
        val displayName = queryDisplayName(context = context, uri = imageUri)
        val hintExtension = extensionFromPath(path = displayName)
            ?: extensionFromPath(path = imageUri.path)
        val providerMimeType = FileUtils.getMimeType(context = context, uri = imageUri)
        val sniffedMimeType = sniffMimeType(context = context, uri = imageUri)
            ?: imageUri.path?.takeIf { imageUri.scheme == "file" }?.let { path ->
                FileUtils.getMimeType(filePath = path)
            }
        val imageMeta = resolveImageMeta(
            sniffedMimeType = sniffedMimeType,
            providerMimeType = providerMimeType,
            hintExtension = hintExtension
        )
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return false
        return inputStream.use { stream ->
            insertImageToAlbum(
                context = context,
                inputStream = stream,
                mimeType = imageMeta.mimeType,
                extension = imageMeta.extension
            )
        }
    }

    private fun queryDisplayName(context: Context, uri: Uri): String? {
        if (uri.scheme != "content") {
            return null
        }
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex >= 0) {
                    return cursor.getString(columnIndex)
                }
            }
        }
        return null
    }

    private fun sniffMimeType(context: Context, uri: Uri): String? {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            val mimeType = options.outMimeType
            if (!mimeType.isNullOrBlank()) {
                return mimeType
            }
        }
        return null
    }

    private fun insertImageToAlbum(
        context: Context,
        inputStream: InputStream,
        mimeType: String,
        extension: String
    ): Boolean {
        val displayName = FileUtils.createFileName()
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${displayName}.${extension}")
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + File.separator + ALBUM_DIRECTORY_NAME
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                @Suppress("DEPRECATION")
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    ALBUM_DIRECTORY_NAME
                )
                if (!directory.exists() && !directory.mkdirs()) {
                    return false
                }
                val imageFile = File(directory, "${displayName}.${extension}")
                @Suppress("DEPRECATION")
                put(MediaStore.MediaColumns.DATA, imageFile.absolutePath)
            }
        }
        val uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return false
        return try {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream == null) {
                contentResolver.delete(uri, null, null)
                return false
            }
            outputStream.use { stream ->
                inputStream.copyTo(stream)
                stream.flush()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }
            true
        } catch (e: Throwable) {
            contentResolver.delete(uri, null, null)
            throw e
        }
    }

    private fun resolveImageMeta(
        sniffedMimeType: String?,
        providerMimeType: String?,
        hintExtension: String?
    ): ImageMeta {
        val hintMimeType = hintExtension?.let { extension ->
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        val mimeType = sniffedMimeType
            ?: providerMimeType
            ?: hintMimeType
            ?: FALLBACK_MIME_TYPE
        val extensionFromMime = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val extension = when {
            !hintExtension.isNullOrBlank() && isExtensionCompatible(
                mimeType = mimeType,
                extension = hintExtension
            ) -> hintExtension

            !extensionFromMime.isNullOrBlank() -> extensionFromMime
            !hintExtension.isNullOrBlank() -> hintExtension
            else -> FALLBACK_EXTENSION
        }
        return ImageMeta(mimeType = mimeType, extension = extension)
    }

    private fun isExtensionCompatible(mimeType: String, extension: String): Boolean {
        val mimeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: return false
        return mimeFromExtension.equals(mimeType, ignoreCase = true)
    }

    private fun extensionFromPath(path: String?): String? {
        if (path.isNullOrBlank()) {
            return null
        }
        val name = path.substringAfterLast(delimiter = '/')
        val extension = name.substringAfterLast(delimiter = '.', missingDelimiterValue = "")
            .lowercase(Locale.ENGLISH)
        if (extension.isBlank() || extension.length > 8 || extension.contains(other = " ")) {
            return null
        }
        return extension
    }

    private data class ImageMeta(
        val mimeType: String,
        val extension: String
    )

}
