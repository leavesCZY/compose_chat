package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

@Stable
data class ProfileUpdatePageViewState(
    val personProfile: PersonProfile?,
    val onNicknameChanged: (nickname: String) -> Unit,
    val onSignatureChanged: (signature: String) -> Unit,
    val onClickSetRandomAvatar: () -> Unit,
    val onConfirmUpdate: () -> Unit
)