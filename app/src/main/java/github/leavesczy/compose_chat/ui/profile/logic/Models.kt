package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class ProfileUpdatePageViewStata(
    val personProfile: PersonProfile?,
    val onNicknameChanged: (nickname: String) -> Unit,
    val onSignatureChanged: (signature: String) -> Unit,
    val setRandomAvatar: () -> Unit,
    val onConfirmUpdate: () -> Unit
)