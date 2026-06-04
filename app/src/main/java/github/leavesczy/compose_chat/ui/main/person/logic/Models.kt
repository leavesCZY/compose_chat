package github.leavesczy.compose_chat.ui.main.person.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Stable
data class PersonProfilePageViewState(
    val personProfile: PersonProfile,
    val onClickPreviewImage: (imageUrl: String) -> Unit
)