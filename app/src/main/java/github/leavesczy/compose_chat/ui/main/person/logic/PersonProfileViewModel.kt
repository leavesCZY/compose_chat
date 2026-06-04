package github.leavesczy.compose_chat.ui.main.person.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.ui.main.logic.BaseMainViewModel
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
abstract class PersonProfileViewModel : BaseMainViewModel() {

    var profilePageViewState by mutableStateOf(
        value = PersonProfilePageViewState(
            personProfile = PersonProfile.Empty,
            onClickPreviewImage = ::previewImage
        )
    )
        private set

    init {
        viewModelScope.launch {
            ComposeChat.accountProvider.personProfileFlow.collect { personProfile ->
                profilePageViewState = profilePageViewState.copy(personProfile = personProfile)
            }
        }
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
        }
    }

}