package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ProfileUpdateViewModel : BaseViewModel() {

    var profileUpdatePageViewStata by mutableStateOf(
        value = ProfileUpdatePageViewStata(
            personProfile = null,
            onNicknameChanged = ::onNicknameChanged,
            onSignatureChanged = ::onSignatureChanged,
            onAvatarUrlChanged = ::onAvatarUrlChanged,
            confirmUpdate = ::confirmUpdate
        )
    )
        private set

    init {
        viewModelScope.launch {
            val profile = ComposeChat.accountProvider.getPersonProfile()
            profileUpdatePageViewStata = profileUpdatePageViewStata.copy(personProfile = profile)
        }
    }

    private fun onNicknameChanged(nickname: String) {
        val viewStata = profileUpdatePageViewStata
        val personProfile = viewStata.personProfile
        if (personProfile != null) {
            profileUpdatePageViewStata =
                viewStata.copy(personProfile = personProfile.copy(nickname = nickname))
        }
    }

    private fun onSignatureChanged(signature: String) {
        val viewStata = profileUpdatePageViewStata
        val personProfile = viewStata.personProfile
        if (personProfile != null) {
            profileUpdatePageViewStata =
                viewStata.copy(personProfile = personProfile.copy(signature = signature))
        }
    }

    private fun onAvatarUrlChanged(imageUrl: String) {
        val viewStata = profileUpdatePageViewStata
        val personProfile = viewStata.personProfile
        if (personProfile != null) {
            profileUpdatePageViewStata =
                viewStata.copy(personProfile = personProfile.copy(faceUrl = imageUrl))
        }
    }

    private fun confirmUpdate() {
        viewModelScope.launch {
            val personProfile = profileUpdatePageViewStata.personProfile
            if (personProfile != null) {
                val result = ComposeChat.accountProvider.updatePersonProfile(
                    faceUrl = personProfile.faceUrl,
                    nickname = personProfile.nickname,
                    signature = personProfile.signature
                )
                when (result) {
                    is ActionResult.Success -> {
                        showToast(msg = "更新成功")
                    }

                    is ActionResult.Failed -> {
                        showToast(msg = result.reason)
                    }
                }
            }
        }
    }

}