package github.leavesczy.compose_chat.base.model

import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 17:05
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
data class PersonProfile(
    val id: String,
    val faceUrl: String,
    val nickname: String,
    val remark: String,
    val signature: String,
    val isFriend: Boolean = false
) {

    companion object {

        val Empty = PersonProfile(
            id = "",
            faceUrl = "",
            nickname = "",
            remark = "",
            signature = "",
            isFriend = false
        )

    }

    val showName: String
        get() {
            return remark.takeIf { it.isNotBlank() }
                ?: nickname.takeIf { it.isNotBlank() }
                ?: id
        }

}

data class GroupMemberProfile(
    val detail: PersonProfile,
    val role: String,
    val isOwner: Boolean,
    val joinTime: Long
) {

    val joinTimeFormat =
        TimeUtil.formatTime(time = joinTime * 1000, format = TimeUtil.YYYY_MM_DD_HH_MM_SS)

}

data class GroupProfile(
    val id: String,
    val faceUrl: String,
    val name: String,
    val introduction: String,
    val createTime: Long,
    val memberCount: Int
) {

    companion object {

        val Empty = GroupProfile(
            id = "",
            faceUrl = "",
            name = "",
            introduction = "",
            createTime = 0L,
            memberCount = 0
        )

    }

    val createTimeFormat =
        TimeUtil.formatTime(time = createTime * 1000, format = TimeUtil.YYYY_MM_DD_HH_MM_SS)

}