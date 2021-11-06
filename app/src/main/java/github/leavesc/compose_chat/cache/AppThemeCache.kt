package github.leavesc.compose_chat.cache

import android.content.Context
import github.leavesc.compose_chat.model.AppTheme
import github.leavesc.compose_chat.utils.ContextHolder

/**
 * @Author: leavesC
 * @Date: 2021/7/9 23:59
 * @Desc:
 * @Github：https://github.com/leavesC
 */
object AppThemeCache {

    private const val KEY_GROUP = "AppThemeGroup"

    private const val KEY_APP_THEME = "keyAppTheme"

    private val DefaultAppTheme = AppTheme.Blue

    private val preferences by lazy {
        ContextHolder.context.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
    }

    var currentTheme = DefaultAppTheme
        private set

    fun init() {
        currentTheme = getAppTheme()
    }

    private fun getAppTheme(): AppTheme {
        val themeType = preferences.getInt(KEY_APP_THEME, DefaultAppTheme.type)
        return AppTheme.values().find { it.type == themeType } ?: DefaultAppTheme
    }

    fun onAppThemeChanged(appTheme: AppTheme) {
        preferences.edit().putInt(KEY_APP_THEME, appTheme.type).apply()
        currentTheme = appTheme
    }

}