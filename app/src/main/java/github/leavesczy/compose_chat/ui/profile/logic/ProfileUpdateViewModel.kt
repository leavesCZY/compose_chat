package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.di.ComposeChat
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.utils.randomImage
import kotlinx.coroutines.launch

class ProfileUpdateViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = ProfileUpdatePageViewState(
            personProfile = null,
            loadFailed = false,
            onNicknameChanged = ::onNicknameChanged,
            onSignatureChanged = ::onSignatureChanged,
            onClickSetRandomAvatar = ::setRandomAvatar,
            onConfirmUpdate = ::onConfirmUpdate
        )
    )
        private set

    private var isUpdating = false

    init {
        viewModelScope.launch {
            showLoadingDialog()
            val profile = ComposeChat.accountProvider.getPersonProfile()
            pageViewState = if (profile == null) {
                pageViewState.copy(personProfile = null, loadFailed = true)
            } else {
                pageViewState.copy(personProfile = profile, loadFailed = false)
            }
            dismissLoadingDialog()
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
        if (isUpdating) {
            return
        }
        viewModelScope.launch {
            isUpdating = true
            showLoadingDialog()
            try {
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
            } finally {
                dismissLoadingDialog()
                isUpdating = false
            }
        }
    }

}
