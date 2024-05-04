package github.leavesczy.compose_chat.base.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
sealed class Chat(open val id: String) : Parcelable {

    @Parcelize
    class PrivateChat(override val id: String) : Chat(id = id)

    @Parcelize
    class GroupChat(override val id: String) : Chat(id = id)

}