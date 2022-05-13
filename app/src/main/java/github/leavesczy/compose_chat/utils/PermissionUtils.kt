package github.leavesczy.compose_chat.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

/**
 * @Author: leavesCZY
 * @Date: 2022/5/13 11:25
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object PermissionUtils {

    fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return false
        }
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

}