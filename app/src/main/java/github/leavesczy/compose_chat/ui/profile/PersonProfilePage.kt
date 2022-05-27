package github.leavesczy.compose_chat.ui.profile

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import github.leavesczy.compose_chat.model.PersonProfilePageState

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonProfilePage(personProfilePageState: PersonProfilePageState) {
    Scaffold {
        ProfilePanel(personProfile = personProfilePageState.personProfile)
    }
}