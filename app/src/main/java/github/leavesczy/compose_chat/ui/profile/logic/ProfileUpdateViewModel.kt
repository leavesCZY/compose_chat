package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

    val profileUpdatePageViewStata by mutableStateOf(
        value = ProfileUpdatePageViewStata(
            personProfile = mutableStateOf(value = null),
            onNicknameChanged = ::onNicknameChanged,
            onSignatureChanged = ::onSignatureChanged,
            onAvatarUrlChanged = ::onAvatarUrlChanged,
            confirmUpdate = ::confirmUpdate
        )
    )

    init {
        viewModelScope.launch {
            val profile = ComposeChat.accountProvider.getPersonProfile()
            profileUpdatePageViewStata.personProfile.value = profile
        }
    }

    private fun onNicknameChanged(nickname: String) {
        val personProfile = profileUpdatePageViewStata.personProfile.value
        if (personProfile != null) {
            profileUpdatePageViewStata.personProfile.value = personProfile.copy(nickname = nickname)
        }
    }

    private fun onSignatureChanged(signature: String) {
        val personProfile = profileUpdatePageViewStata.personProfile.value
        if (personProfile != null) {
            profileUpdatePageViewStata.personProfile.value =
                personProfile.copy(signature = signature)
        }
    }

    private fun onAvatarUrlChanged(imageUrl: String) {
        val personProfile = profileUpdatePageViewStata.personProfile.value
        if (personProfile != null) {
            profileUpdatePageViewStata.personProfile.value = personProfile.copy(faceUrl = imageUrl)
        }
    }

    private fun confirmUpdate() {
        viewModelScope.launch {
            val personProfile = profileUpdatePageViewStata.personProfile.value
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