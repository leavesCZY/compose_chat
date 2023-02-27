package github.leavesczy.compose_chat.base.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class Chat(open val id: String) : Parcelable {

    @Parcelize
    class PrivateChat(override val id: String) : Chat(id = id)

    @Parcelize
    class GroupChat(override val id: String) : Chat(id = id)

}