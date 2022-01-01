package github.leavesc.compose_chat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

/**
 * @Author: leavesC
 * @Date: 2022/1/1 13:10
 * @Desc:
 */
object BitmapUtils {

    private val randomFileName: String
        get() = (System.currentTimeMillis() + Random.nextInt(2022, 6000)).toString()

    fun saveImage(context: Context, imageUri: Uri): File? {
        try {
            val bitmap = decodeBitmap(context = context, imageUri = imageUri)
            if (bitmap != null) {
                return saveBitmap(context = context, bitmap = bitmap)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun saveBitmap(context: Context, bitmap: Bitmap): File? {
        return try {
            val imageFile = File(context.cacheDir, "$randomFileName.jpg")
            if (imageFile.exists()) {
                imageFile.delete()
            }
            imageFile.createNewFile()
            copyBitmap(bitmap = bitmap, desc = imageFile)
            imageFile
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    fun decodeBitmap(context: Context, imageUri: Uri): Bitmap? {
        val fileDescriptor = context.contentResolver?.openFileDescriptor(imageUri, "r")
        if (fileDescriptor != null) {
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor)
            fileDescriptor.close()
            return bitmap
        }
        return null
    }

    private fun copyBitmap(bitmap: Bitmap, desc: File) {
        val op = FileOutputStream(desc)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, op)
    }

}