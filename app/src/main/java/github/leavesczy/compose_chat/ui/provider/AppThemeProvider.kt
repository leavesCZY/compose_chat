package github.leavesczy.compose_chat.ui.provider

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import github.leavesczy.compose_chat.theme.AppThemeMode

object AppThemeProvider {

    private const val KEY_GROUP = "AppThemeGroup"

    private const val KEY_APP_THEME_MODE = "keyAppThemeMode"

    private lateinit var preferences: SharedPreferences

    private val defaultThemeMode = AppThemeMode.Light

    var appThemeMode by mutableStateOf(value = defaultThemeMode)
        private set

    fun init(application: Application) {
        preferences = application.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
        appThemeMode = getAppThemeModeOfDefault()
        initThemeDelegate(appThemeMode = appThemeMode)
    }

    private fun getAppThemeModeOfDefault(): AppThemeMode {
        val themeModeIndex = preferences.getInt(KEY_APP_THEME_MODE, defaultThemeMode.ordinal)
        return AppThemeMode.entries.find { theme ->
            theme.ordinal == themeModeIndex
        } ?: defaultThemeMode
    }

    fun onAppThemeModeChanged(appThemeMode: AppThemeMode) {
        preferences.edit {
            putInt(KEY_APP_THEME_MODE, appThemeMode.ordinal)
        }
        initThemeDelegate(appThemeMode = appThemeMode)
        this.appThemeMode = appThemeMode
    }

    private fun initThemeDelegate(appThemeMode: AppThemeMode) {
        when (appThemeMode) {
            AppThemeMode.Light, AppThemeMode.Gray -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            AppThemeMode.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

}