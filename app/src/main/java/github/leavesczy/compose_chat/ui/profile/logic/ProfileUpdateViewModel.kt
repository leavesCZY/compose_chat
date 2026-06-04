package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.BaseViewModel
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.randomImage
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
class ProfileUpdateViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = ProfileUpdatePageViewState(
            personProfile = null,
            onNicknameChanged = ::onNicknameChanged,
            onSignatureChanged = ::onSignatureChanged,
            onClickSetRandomAvatar = ::setRandomAvatar,
            onConfirmUpdate = ::onConfirmUpdate
        )
    )
        private set

    init {
        viewModelScope.launch {
            val profile = ComposeChat.accountProvider.getPersonProfile()
            pageViewState = pageViewState.copy(personProfile = profile)
        }
    }

    private fun onNicknameChanged(nickname: String) {
        val viewState = pageViewState
        val personProfile = viewState.personProfile
        if (personProfile != null) {
            pageViewState =
                viewState.copy(personProfile = personProfile.copy(nickname = nickname))
        }
    }

    private fun onSignatureChanged(signature: String) {
        val viewState = pageViewState
        val personProfile = viewState.personProfile
        if (personProfile != null) {
            pageViewState =
                viewState.copy(personProfile = personProfile.copy(signature = signature))
        }
    }

    private fun setRandomAvatar() {
        val viewState = pageViewState
        val personProfile = viewState.personProfile
        if (personProfile != null) {
            pageViewState =
                viewState.copy(personProfile = personProfile.copy(avatarUrl = randomImage()))
        }
    }

    private fun onConfirmUpdate() {
        viewModelScope.launch {
            showLoadingDialog()
            val personProfile = pageViewState.personProfile
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
