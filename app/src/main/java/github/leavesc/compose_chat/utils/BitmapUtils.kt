package github.leavesc.compose_chat.utils

import android.content.Context
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
            val inputStream = context.contentResolver?.openInputStream(imageUri) ?: return null
            val imageFile =
                File(context.externalCacheDir ?: context.cacheDir, "$randomFileName.jpg")
            if (imageFile.exists()) {
                imageFile.delete()
            }
            imageFile.createNewFile()
            val outputStream = FileOutputStream(imageFile)
            inputStream.copyTo(out = outputStream)
            inputStream.close()
            outputStream.close()
            return imageFile
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

}