package github.leavesc.compose_chat.base.model

/**
 * @Author: leavesC
 * @Date: 2021/6/22 17:05
 * @Desc:
 * @Github：https://github.com/leavesC
 */
open class BaseProfile(
    val userId: String,
    val faceUrl: String,
    val nickname: String,
    val remark: String,
    val signature: String,
)

class PersonProfile(
    userId: String,
    faceUrl: String,
    nickname: String,
    remark: String,
    signature: String,
) : BaseProfile(
    userId = userId,
    faceUrl = faceUrl,
    nickname = nickname,
    remark = remark,
    signature = signature
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

class GroupMemberProfile(
    userId: String,
    faceUrl: String,
    nickname: String,
    remark: String,
    signature: String,
    val role: String,
    val joinTime: Long
) : BaseProfile(
    userId = userId,
    faceUrl = faceUrl,
    nickname = nickname,
    remark = remark,
    signature = signature
)

data class GroupProfile(
    val id: String,
    val faceUrl: String,
    val name: String,
    val notification: String
) {

    companion object {

        val Empty = GroupProfile(id = "", faceUrl = "", name = "", notification = "")

    }

}