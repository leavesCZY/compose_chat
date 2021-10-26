package github.leavesc.compose_chat.ui.person

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.ui.profile.ProfileScreen

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun PersonProfileScreen(personProfile: PersonProfile) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ProfileScreen(personProfile = personProfile)
    }
}