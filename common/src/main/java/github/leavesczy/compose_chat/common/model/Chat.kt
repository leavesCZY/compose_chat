package github.leavesczy.compose_chat.common.model

/**
 * @Author: leavesCZY
 * @Date: 2021/10/27 14:09
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class Chat(val type: Int, val id: String) {

    companion object {

        internal const val PRIVATE_CHAT = 1

        internal const val GROUP_CHAT = 2

        fun find(type: Int, id: String): Chat {
            when (type) {
                PRIVATE_CHAT -> {
                    return PrivateChat(id = id)
                }
                GROUP_CHAT -> {
                    return GroupChat(id = id)
                }
            }
            throw IllegalArgumentException()
        }

    }

}

class PrivateChat(id: String) : Chat(PRIVATE_CHAT, id)

class GroupChat(id: String) : Chat(GROUP_CHAT, id)