package github.leavesczy.compose_chat.provider

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import github.leavesczy.compose_chat.ui.main.logic.AppTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object AppThemeProvider {

    private const val KEY_GROUP = "AppThemeGroup"

    private const val KEY_APP_THEME = "keyAppTheme"

    private lateinit var preferences: SharedPreferences

    var appTheme by mutableStateOf(value = AppTheme.Default)
        private set

    fun init(application: Application) {
        preferences = application.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
        appTheme = getAppThemeOfDefault()
        initThemeDelegate(appTheme = appTheme)
    }

    private fun getAppThemeOfDefault(): AppTheme {
        val themeIndex = preferences.getInt(KEY_APP_THEME, AppTheme.Default.ordinal)
        return AppTheme.values().find { it.ordinal == themeIndex } ?: AppTheme.Default
    }

    fun onAppThemeChanged(appTheme: AppTheme) {
        preferences.edit().putInt(KEY_APP_THEME, appTheme.ordinal).apply()
        this.appTheme = appTheme
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