package github.leavesc.compose_chat.model

/**
 * @Author: leavesC
 * @Date: 2021/7/27 10:39
 * @Desc:
 * @Github：https://github.com/leavesC
 */
enum class AppTheme(val type: Int) {

    Light(0), Dark(1), Gray(2);

    fun nextTheme(): AppTheme {
        val values = values()
        return values.getOrElse(type + 1) {
            values[0]
        }
    }

}