package github.leavesczy.compose_chat.cache

import android.content.Context
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.utils.ContextHolder

/**
 * @Author: leavesCZY
 * @Date: 2021/7/9 23:59
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object AppThemeCache {

    private const val KEY_GROUP = "AppThemeGroup"

    private const val KEY_APP_THEME = "keyAppTheme"

    private val preferences by lazy {
        ContextHolder.context.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
    }

    var currentTheme = AppTheme.DefaultAppTheme
        private set

    fun init() {
        currentTheme = getAppTheme()
    }

    private fun getAppTheme(): AppTheme {
        val themeIndex = preferences.getInt(KEY_APP_THEME, AppTheme.DefaultAppTheme.ordinal)
        return AppTheme.values().find { it.ordinal == themeIndex } ?: AppTheme.DefaultAppTheme
    }

    fun onAppThemeChanged(appTheme: AppTheme) {
        preferences.edit().putInt(KEY_APP_THEME, appTheme.ordinal).apply()
        currentTheme = appTheme
    }

}