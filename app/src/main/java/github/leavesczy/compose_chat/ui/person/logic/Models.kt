package github.leavesczy.compose_chat.ui.person.logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

@Stable
data class PersonProfilePageViewState(
    val personProfile: MutableState<PersonProfile>,
    val previewImage: (String) -> Unit
)