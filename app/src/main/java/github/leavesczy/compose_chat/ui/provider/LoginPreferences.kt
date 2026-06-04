package github.leavesczy.compose_chat.ui.provider

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
object LoginPreferences {

    private const val KEY_GROUP = "AccountGroup"

    private const val KEY_LAST_LOGIN_USER_ID = "keyLastLoginUserId"

    private const val KEY_AUTO_LOGIN = "keyAutoLogin"

    private lateinit var preferences: SharedPreferences

    val lastLoginUserId: String
        get() = preferences.getString(KEY_LAST_LOGIN_USER_ID, "") ?: ""

    val isAutoLoginEnabled: Boolean
        get() = preferences.getBoolean(KEY_AUTO_LOGIN, true)

    fun init(application: Application) {
        preferences = application.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
    }

    fun onUserLogin(userId: String) {
        preferences.edit {
            putString(KEY_LAST_LOGIN_USER_ID, userId)
            putBoolean(KEY_AUTO_LOGIN, true)
        }
    }

    fun onUserLogout() {
        preferences.edit {
            putBoolean(KEY_AUTO_LOGIN, false)
        }
    }

}
