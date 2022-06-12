package github.leavesczy.compose_chat.ui.profile

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import github.leavesczy.compose_chat.model.PersonProfilePageState

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun PersonProfilePage(personProfilePageState: PersonProfilePageState) {
    Scaffold { contentPadding ->
        ProfilePanel(
            personProfile = personProfilePageState.personProfile,
            contentPadding = contentPadding
        )
    }
}