package github.leavesczy.compose_chat.ui.person

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import github.leavesczy.compose_chat.model.PersonProfileScreenState
import github.leavesczy.compose_chat.ui.profile.ProfileScreen

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun PersonProfileScreen(personProfileScreenState: PersonProfileScreenState) {
    Scaffold {
        ProfileScreen(personProfile = personProfileScreenState.personProfile)
    }
}