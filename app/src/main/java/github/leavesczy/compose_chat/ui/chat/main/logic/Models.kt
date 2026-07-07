package github.leavesczy.compose_chat.ui.chat.main.logic

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Message
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow

@Stable
data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val scrollToLatestMessageFlow: Flow<Long>,
    val topBarTitle: String,
    val messageList: PersistentList<Message>,
    val onClickAvatar: (message: Message) -> Unit,
    val onClickMessage: (message: Message) -> Unit
)

@Stable
data class ChatPageBottomBarViewState(
    val isPhotoPickerAvailable: Boolean,
    val inputSelector: InputSelector,
    val onInputSelectorChanged: (inputSelector: InputSelector) -> Unit,
    val onSendTextMessage: (text: String) -> Unit,
    val onSendImageMessage: (imageUri: Uri) -> Unit
)

@Stable
enum class InputSelector {
    None,
    Emoji,
    Picture;
}

@Stable
data class LoadMessageViewState(
    val isRefreshing: Boolean,
    val isLoadFinished: Boolean,
    val onLoadMoreMessage: () -> Unit
)