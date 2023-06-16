package github.leavesczy.compose_chat.ui.person.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class PersonProfileViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = PersonProfilePageViewState(
            personProfile = PersonProfile.Empty,
            previewImage = ::previewImage
        )
    )
        private set

    init {
        viewModelScope.launch {
            ComposeChat.accountProvider.personProfile.collect {
                pageViewState = pageViewState.copy(personProfile = it)
            }
        }
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imagePath = imageUrl)
        }
    }

}