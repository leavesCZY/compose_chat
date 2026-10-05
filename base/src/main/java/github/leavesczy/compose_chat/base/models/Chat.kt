package github.leavesczy.compose_chat.base.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Chat(open val id: String) : Parcelable {

    data class C2C(override val id: String) : Chat(id = id)

    data class Group(override val id: String) : Chat(id = id)

}
