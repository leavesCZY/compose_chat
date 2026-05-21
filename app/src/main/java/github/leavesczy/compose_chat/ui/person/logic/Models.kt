package github.leavesczy.compose_chat.ui.person.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class PersonProfilePageViewState(
    val personProfile: PersonProfile,
    val previewImage: (imageUrl: String) -> Unit
)