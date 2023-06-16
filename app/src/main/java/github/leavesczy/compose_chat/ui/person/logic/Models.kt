package github.leavesczy.compose_chat.ui.person.logic

import github.leavesczy.compose_chat.base.model.PersonProfile

data class PersonProfilePageViewState(
    val personProfile: PersonProfile,
    val previewImage: (String) -> Unit
)