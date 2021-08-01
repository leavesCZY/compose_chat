package github.leavesc.compose_chat.proxy.consts

import github.leavesc.compose_chat.base.model.PersonProfile
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @Author: leavesC
 * @Date: 2021/6/7 19:38
 * @Desc:
 * @Github：https://github.com/leavesC
 */
internal object AppConst {

    const val APP_ID = 1400553975

    const val APP_SECRET_KEY = "36b54858b7422f77243c595d0dc07a7a72d60e224c833d9d02687493a1e38a40"

    val userProfile = MutableStateFlow(PersonProfile.Empty)

}