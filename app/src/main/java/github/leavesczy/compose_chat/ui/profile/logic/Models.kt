package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

@Stable
data class ProfileUpdatePageViewStata(
    val personProfile: PersonProfile?,
    val onNicknameChanged: (String) -> Unit,
    val onSignatureChanged: (String) -> Unit,
    val onAvatarUrlChanged: (String) -> Unit,
    val confirmUpdate: () -> Unit
)