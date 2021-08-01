package github.leavesc.compose_chat.model

/**
 * @Author: leavesC
 * @Date: 2021/7/27 10:39
 * @Desc:
 * @Github：https://github.com/leavesC
 */
enum class AppTheme(val type: Int) {

    Light(0), Dark(1), Pink(2);

    fun isDarkTheme(): Boolean {
        return this == Dark
    }

    fun nextTheme(): AppTheme {
        return when (this) {
            Light -> {
                Dark
            }
            Dark -> {
                Pink
            }
            Pink -> {
                Light
            }
        }
    }

}