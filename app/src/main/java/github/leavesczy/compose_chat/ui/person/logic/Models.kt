package github.leavesczy.compose_chat.ui.person.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.model.PersonProfile

@Stable
data class PersonProfilePageViewState(
    val personProfile: PersonProfile,
    val previewImage: (String) -> Unit
)