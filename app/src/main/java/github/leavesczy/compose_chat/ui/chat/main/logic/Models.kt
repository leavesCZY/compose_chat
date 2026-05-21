package github.leavesczy.compose_chat.ui.chat.main.logic

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Message
import kotlinx.collections.immutable.PersistentList

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val topBarTitle: String,
    val messageList: PersistentList<Message>,
    val onClickAvatar: (activity: Activity, message: Message) -> Unit,
    val onClickMessage: (activity: Activity, message: Message) -> Unit
)

@Stable
data class ChatPageBottomBarViewState(
    val isPhotoPickerAvailable: Boolean,
    val inputSelector: InputSelector,
    val onInputSelectorChanged: (inputSelector: InputSelector) -> Unit,
    val sendTextMessage: (text: String) -> Unit,
    val sendImageMessage: (imageUri: Uri) -> Unit
)

@Stable
enum class InputSelector {
    NONE,
    EMOJI,
    Picture;
}

@Stable
data class LoadMessageViewState(
    val refreshing: Boolean,
    val loadFinish: Boolean,
    val loadMoreMessage: () -> Unit
)