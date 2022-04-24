package github.leavesczy.compose_chat.model

/**
 * @Author: leavesCZY
 * @Date: 2021/7/27 10:39
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
enum class AppTheme {

    Light, Dark, Gray;

    companion object {

        val DefaultAppTheme = Light

    }

    fun nextTheme(): AppTheme {
        val values = values()
        return values.getOrElse(ordinal + 1) {
            values[0]
        }
    }

}