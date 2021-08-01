package github.leavesc.compose_chat.base.model

/**
 * @Author: leavesC
 * @Date: 2021/6/22 17:05
 * @Desc:
 * @Github：https://github.com/leavesC
 */
data class PersonProfile(
    val userId: String,
    val faceUrl: String,
    val nickname: String,
    val remark: String,
    val signature: String,
) {

    companion object {

        val Empty =
            PersonProfile(userId = "", faceUrl = "", nickname = "", remark = "", signature = "")

    }

    val showName: String
        get() {
            return remark.takeIf { it.isNotBlank() }
                ?: nickname.takeIf { it.isNotBlank() }
                ?: userId
        }

}

data class GroupMemberProfile(
    val userId: String,
    val faceUrl: String,
    val nickname: String,
    val remark: String,
    val signature: String,
    val role: String,
    val joinTime: Long
)

data class GroupProfile(
    val id: String,
    val faceUrl: String,
    val name: String,
    val notification: String
)