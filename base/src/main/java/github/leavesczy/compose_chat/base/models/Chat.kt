package github.leavesczy.compose_chat.base.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Parcelize
@Stable
sealed class Chat(open val id: String) : Parcelable {

    @Stable
    data class C2C(override val id: String) : Chat(id = id)

    @Stable
    data class Group(override val id: String) : Chat(id = id)

}