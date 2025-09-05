package github.leavesczy.compose_chat.provider

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object AccountProvider {

    private const val KEY_GROUP = "AccountGroup"

    private const val KEY_LAST_LOGIN_USER_ID = "keyLastLoginUserId"

    private const val KEY_AUTO_LOGIN = "keyAutoLogin"

    private lateinit var preferences: SharedPreferences

    fun init(application: Application) {
        preferences = application.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
    }

    val lastLoginUserId: String
        get() = preferences.getString(KEY_LAST_LOGIN_USER_ID, "") ?: ""

    val canAutoLogin: Boolean
        get() = preferences.getBoolean(KEY_AUTO_LOGIN, true)

    fun onUserLogin(userId: String) {
        preferences.edit().apply {
            putString(KEY_LAST_LOGIN_USER_ID, userId)
            putBoolean(KEY_AUTO_LOGIN, true)
            apply()
        }
    }

    fun onUserLogout() {
        preferences.edit { putBoolean(KEY_AUTO_LOGIN, false) }
    }

}