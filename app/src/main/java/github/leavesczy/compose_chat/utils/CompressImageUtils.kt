package github.leavesczy.compose_chat.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

object CompressImageUtils {

    private const val IMAGE_MAX_SIZE = 1024 * 1024

    private const val MAX_PIXEL_COUNT = 1920 * 1920

    private const val JPEG_MIME_TYPE = "image/jpeg"

    private const val GIF_MIME_TYPE = "image/gif"

    private const val WEBP_MIME_TYPE = "image/webp"

    private const val JPEG = "jpeg"

    private const val MIN_COMPRESS_QUALITY = 10

    private const val TEMP_FILE_MAX_AGE_MS = 24L * 60L * 60L * 1000L

    private const val TEMP_FILE_MAX_COUNT = 30

    private val protectedTempPaths = java.util.Collections.synchronizedSet(mutableSetOf<String>())

    suspend fun compressImage(context: Context, imageUri: Uri): File? {
        return withContext(context = Dispatchers.IO) {
            try {
                cleanupOldTempFiles(context = context)
                val mimeType = FileUtils.getMimeType(context = context, uri = imageUri)
                    ?: JPEG_MIME_TYPE
                val fileSize = context.contentResolver.openFileDescriptor(imageUri, "r")
                    ?.use { parcelFileDescriptor ->
                        parcelFileDescriptor.statSize
                    } ?: return@withContext null
                if (mimeType == GIF_MIME_TYPE || isAnimatedWebP(
                        context = context,
                        uri = imageUri,
                        mimeType = mimeType
                    )
                ) {
                    return@withContext resolveUploadFile(
                        context = context,
                        imageUri = imageUri,
                        mimeType = mimeType
                    )
                }
                val bounds = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                val boundsDescriptor = context.contentResolver.openFileDescriptor(imageUri, "r")
                    ?: return@withContext null
                boundsDescriptor.use { parcelFileDescriptor ->
                    BitmapFactory.decodeFileDescriptor(
                        parcelFileDescriptor.fileDescriptor,
                        null,
                        bounds
                    )
                }
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                    return@withContext null
                }
                val oversizedPixels =
                    bounds.outWidth.toLong() * bounds.outHeight.toLong() > MAX_PIXEL_COUNT
                val mustCompress = fileSize > IMAGE_MAX_SIZE || oversizedPixels
                if (mustCompress) {
                    compressToJpeg(
                        context = context,
                        imageUri = imageUri,
                        bounds = bounds
                    )
                } else {
                    resolveUploadFile(
                        context = context,
                        imageUri = imageUri,
                        mimeType = mimeType
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }
    }

    fun deleteTempFileIfNeeded(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        unprotectTempFile(file = file)
        val parent = file.parentFile ?: return
        val picturesDirName = Environment.DIRECTORY_PICTURES
        if (parent.name == picturesDirName || parent.absolutePath.contains(picturesDirName)) {
            runCatching {
                file.delete()
            }
        }
    }

    fun protectTempFile(file: File?) {
        if (file == null) {
            return
        }
        protectedTempPaths.add(file.absolutePath)
    }

    fun unprotectTempFile(file: File?) {
        if (file == null) {
            return
        }
        protectedTempPaths.remove(file.absolutePath)
    }

    fun cleanupOldTempFiles(context: Context) {
        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return
        val files = picturesDir.listFiles() ?: return
        val now = System.currentTimeMillis()
        files.filter { file ->
            file.isFile &&
                    !protectedTempPaths.contains(file.absolutePath) &&
                    now - file.lastModified() > TEMP_FILE_MAX_AGE_MS
        }.forEach { file ->
            runCatching { file.delete() }
        }
        val remaining = picturesDir.listFiles()
            ?.filter { file ->
                file.isFile && !protectedTempPaths.contains(file.absolutePath)
            }
            ?.sortedByDescending { file -> file.lastModified() }
            ?: return
        if (remaining.size > TEMP_FILE_MAX_COUNT) {
            remaining.drop(n = TEMP_FILE_MAX_COUNT).forEach { file ->
                runCatching { file.delete() }
            }
        }
    }

    private fun isAnimatedWebP(context: Context, uri: Uri, mimeType: String): Boolean {
        if (mimeType != WEBP_MIME_TYPE) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false
        }
        return runCatching {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val drawable = ImageDecoder.decodeDrawable(source)
            drawable is android.graphics.drawable.AnimatedImageDrawable
        }.getOrDefault(defaultValue = false)
    }

    private suspend fun compressToJpeg(
        context: Context,
        imageUri: Uri,
        bounds: BitmapFactory.Options
    ): File? {
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(
                width = bounds.outWidth,
                height = bounds.outHeight,
                maxPixelCount = MAX_PIXEL_COUNT
            )
        }
        val bitmap = context.contentResolver.openFileDescriptor(imageUri, "r")
            ?.use { parcelFileDescriptor ->
                BitmapFactory.decodeFileDescriptor(
                    parcelFileDescriptor.fileDescriptor,
                    null,
                    decodeOptions
                )
            } ?: return null
        val tempFile = FileUtils.createTempFile(context = context, extension = JPEG)
        try {
            writeCompressedJpeg(
                bitmap = bitmap,
                maxSize = IMAGE_MAX_SIZE,
                targetFile = tempFile
            )
            context.contentResolver.openFileDescriptor(imageUri, "r")?.use { parcelFileDescriptor ->
                FileUtils.copyExifOrientation(
                    sourceImageFileDescriptor = parcelFileDescriptor.fileDescriptor,
                    targetImagePath = tempFile.absolutePath
                )
            }
            return tempFile
        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    private suspend fun resolveUploadFile(
        context: Context,
        imageUri: Uri,
        mimeType: String
    ): File? {
        if (imageUri.scheme == ContentResolver.SCHEME_FILE) {
            val path = imageUri.path
            if (!path.isNullOrBlank()) {
                val file = File(path)
                if (file.exists()) {
                    return file
                }
            }
        }
        val extension = FileUtils.getExtensionFromMimeType(mimeType = mimeType) ?: JPEG
        val tempFile = FileUtils.createTempFile(context = context, extension = extension)
        val inputStream = context.contentResolver.openInputStream(imageUri)
        if (inputStream == null) {
            deleteTempFileIfNeeded(file = tempFile)
            return null
        }
        inputStream.use { stream ->
            tempFile.outputStream().use { outputStream ->
                stream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun writeCompressedJpeg(bitmap: Bitmap, maxSize: Int, targetFile: File) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100
        while (true) {
            byteArrayOutputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            if (byteArrayOutputStream.size() <= maxSize || quality <= MIN_COMPRESS_QUALITY) {
                targetFile.writeBytes(byteArrayOutputStream.toByteArray())
                break
            }
            quality -= 10
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxPixelCount: Int): Int {
        if (width <= 0 || height <= 0) {
            return 1
        }
        var inSampleSize = 1
        var currentWidth = width
        var currentHeight = height
        while (currentWidth * currentHeight > maxPixelCount) {
            inSampleSize *= 2
            currentWidth = width / inSampleSize
            currentHeight = height / inSampleSize
        }
        return inSampleSize.coerceAtLeast(minimumValue = 1)
    }

}
