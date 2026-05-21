package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class PersonProfile(
    val id: String,
    val avatarUrl: String,
    val nickname: String,
    val remark: String,
    val signature: String,
    val addTime: Long,
    val isFriend: Boolean
) {

    companion object {

        val Empty = PersonProfile(
            id = "",
            avatarUrl = "",
            nickname = "",
            remark = "",
            signature = "",
            addTime = 0,
            isFriend = false
        )

    }

    val showName: String
        get() = remark.ifBlank {
            nickname.ifBlank {
                id
            }
        }

}

@Stable
data class GroupMemberProfile(
    val detail: PersonProfile,
    val isOwner: Boolean,
    val joinTime: Long
) {

    val joinTimeFormat = TimeUtil.formatTimeYMDHMS(milliseconds = joinTime)

}

@Stable
data class GroupProfile(
    val id: String,
    val avatarUrl: String,
    val name: String,
    val introduction: String,
    private val createTime: Long
) {

    val createTimeFormat = TimeUtil.formatTimeYMDHMS(milliseconds = createTime)

}