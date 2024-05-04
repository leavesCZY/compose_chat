package github.leavesczy.compose_chat.ui.person.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.PersonProfile
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

    val pageViewState by mutableStateOf(
        value = PersonProfilePageViewState(
            personProfile = mutableStateOf(value = PersonProfile.Empty),
            previewImage = ::previewImage
        )
    )

    init {
        viewModelScope.launch {
            ComposeChat.accountProvider.personProfile.collect {
                pageViewState.personProfile.value = it
            }
        }
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
        }
    }

}