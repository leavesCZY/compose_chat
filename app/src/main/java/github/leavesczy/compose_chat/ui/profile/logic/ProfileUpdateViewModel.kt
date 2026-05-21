package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.utils.randomImage
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class ProfileUpdateViewModel : BaseViewModel() {

    var profileUpdatePageViewStata by mutableStateOf(
        value = ProfileUpdatePageViewStata(
            personProfile = null,
            onNicknameChanged = ::onNicknameChanged,
            onSignatureChanged = ::onSignatureChanged,
            setRandomAvatar = ::setRandomAvatar,
            onConfirmUpdate = ::onConfirmUpdate
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

    private fun setRandomAvatar() {
        val viewStata = profileUpdatePageViewStata
        val personProfile = viewStata.personProfile
        if (personProfile != null) {
            profileUpdatePageViewStata =
                viewStata.copy(personProfile = personProfile.copy(avatarUrl = randomImage()))
        }
    }

    private fun onConfirmUpdate() {
        viewModelScope.launch {
            showLoadingDialog()
            val personProfile = profileUpdatePageViewStata.personProfile
            if (personProfile != null) {
                val result = ComposeChat.accountProvider.updateProfile(
                    avatarUrl = personProfile.avatarUrl,
                    nickname = personProfile.nickname,
                    signature = personProfile.signature
                )
                when (result) {
                    is ActionResult.Success -> {
                        showToast(resId = R.string.toast_update_success)
                    }

                    is ActionResult.Failed -> {
                        showToast(msg = result.desc)
                    }
                }
            }
            dismissLoadingDialog()
        }
    }

}