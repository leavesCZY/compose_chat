package github.leavesczy.compose_chat.ui.person.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:28
 * @Desc:
 */
@Stable
data class PersonProfilePageViewState(
    val personProfile: PersonProfile,
    val previewImage: (String) -> Unit
)