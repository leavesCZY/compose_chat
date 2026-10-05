package github.leavesczy.compose_chat.ui.main.person.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.di.ComposeChat
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import kotlinx.coroutines.launch

class PersonProfileViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = PersonProfilePageViewState(
            personProfile = PersonProfile.Empty,
            onClickPreviewImage = ::previewImage
        )
    )
        private set

    init {
        viewModelScope.launch {
            ComposeChat.accountProvider.personProfileFlow.collect { personProfile ->
                pageViewState = pageViewState.copy(personProfile = personProfile)
            }
        }
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
        }
    }

}
