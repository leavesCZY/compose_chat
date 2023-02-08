package github.leavesczy.compose_chat.cache

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import github.leavesczy.compose_chat.model.AppTheme

/**
 * @Author: leavesCZY
 * @Date: 2021/7/9 23:59
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object AppThemeCache {

    private const val KEY_GROUP = "AppThemeGroup"

    private const val KEY_APP_THEME = "keyAppTheme"

    private lateinit var preferences: SharedPreferences

    var currentTheme = AppTheme.DefaultAppTheme
        private set

    fun init(application: Application) {
        preferences = application.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
        currentTheme = getAppTheme()
        initThemeDelegate(appTheme = currentTheme)
    }

    private fun getAppTheme(): AppTheme {
        val themeIndex = preferences.getInt(KEY_APP_THEME, AppTheme.DefaultAppTheme.ordinal)
        return AppTheme.values().find { it.ordinal == themeIndex } ?: AppTheme.DefaultAppTheme
    }

    fun onAppThemeChanged(appTheme: AppTheme) {
        preferences.edit().putInt(KEY_APP_THEME, appTheme.ordinal).apply()
        currentTheme = appTheme
        initThemeDelegate(appTheme = appTheme)
    }

    private fun initThemeDelegate(appTheme: AppTheme) {
        when (appTheme) {
            AppTheme.Light, AppTheme.Gray -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppTheme.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

}