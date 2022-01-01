package github.leavesc.compose_chat.base.model

/**
 * @Author: leavesC
 * @Date: 2021/10/27 14:09
 * @Desc:
 * @Github：https://github.com/leavesC
 */
sealed class Chat(val type: Int, val id: String) {

    companion object {

        private const val C2C_TYPE = 1

        private const val GROUP_TYPE = 2

        fun find(type: Int, id: String): Chat {
            when (type) {
                C2C_TYPE -> {
                    return C2C(id = id)
                }
                GROUP_TYPE -> {
                    return Group(id = id)
                }
            }
            throw IllegalArgumentException()
        }

    }

    class C2C(id: String) : Chat(C2C_TYPE, id)

    class Group(id: String) : Chat(GROUP_TYPE, id)

}