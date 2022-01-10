package github.leavesc.compose_chat.ui.person

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import github.leavesc.compose_chat.model.PersonProfileScreenState
import github.leavesc.compose_chat.ui.profile.ProfileScreen

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun PersonProfileScreen(personProfileScreenState: PersonProfileScreenState) {
    Scaffold {
        ProfileScreen(personProfile = personProfileScreenState.personProfile)
    }
}