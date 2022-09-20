package github.leavesczy.compose_chat.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.graphics.vector.ImageVector
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.matisse.MediaResource

/**
 * @Author: leavesCZY
 * @Date: 2021/7/6 15:17
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
enum class LoginPageAction {
    LoginSuccess
}

data class LoginPageViewState(
    val showPanel: Boolean,
    val showLoadingDialog: Boolean,
    val lastLoginUserId: String
)

enum class MainTab(val icon: ImageVector) {
    Conversation(
        icon = Icons.Filled.Cabin
    ),
    Friendship(
        icon = Icons.Filled.Sailing,
    ),
    Person(
        icon = Icons.Filled.ColorLens,
    );
}

data class MainPageDrawerViewState(
    val drawerState: DrawerState,
    val appTheme: AppTheme,
    val personProfile: PersonProfile
)

data class MainPageBottomBarViewState(
    val tabList: List<MainTab>,
    val tabSelected: MainTab,
    val unreadMessageCount: Long
)

data class MainPageAction(
    val onClickConversation: (Conversation) -> Unit,
    val onDeleteConversation: (Conversation) -> Unit,
    val onPinnedConversation: (Conversation, Boolean) -> Unit,
    val onClickGroupItem: (GroupProfile) -> Unit,
    val onClickFriendItem: (PersonProfile) -> Unit,
    val onTabSelected: (MainTab) -> Unit,
    val switchToNextTheme: () -> Unit,
    val logout: () -> Unit,
    val changDrawerState: (DrawerValue) -> Unit,
    val showFriendshipPanel: () -> Unit,
)

data class ConversationPageViewState(
    val listState: LazyListState,
    val conversationList: List<Conversation>
)

data class FriendshipPageViewState(
    val listState: LazyListState,
    val joinedGroupList: List<GroupProfile>,
    val friendList: List<PersonProfile>
)

data class PersonProfilePageViewState(
    val personProfile: PersonProfile
)

data class FriendshipPanelViewState(
    val visible: Boolean,
    val onDismissRequest: () -> Unit,
    val joinGroup: (groupId: String) -> Unit,
    val addFriend: (userId: String) -> Unit,
)

data class FriendProfilePageViewState(
    val personProfile: PersonProfile,
    val showAlterBtb: Boolean,
    val showAddBtn: Boolean,
    val navToChat: () -> Unit,
    val addFriend: () -> Unit,
    val deleteFriend: () -> Unit,
    val showSetFriendRemarkPanel: () -> Unit,
)

enum class FriendProfilePageAction {

    FinishActivity

}

data class SetFriendRemarkPanelViewState(
    val visible: Boolean,
    val personProfile: PersonProfile,
    val onDismissRequest: () -> Unit,
    val setRemark: (String) -> Unit
)

data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val topBarTitle: String,
    val messageList: List<Message>,
    val showLoadMore: Boolean,
    val loadFinish: Boolean
)

data class ChatPageAction(
    val onClickBackMenu: () -> Unit,
    val onClickMoreMenu: () -> Unit,
    val sendTextMessage: (String) -> Unit,
    val sendImageMessage: (MediaResource) -> Unit,
    val loadMoreMessage: () -> Unit,
    val onClickAvatar: (Message) -> Unit,
    val onClickMessage: (Message) -> Unit,
    val onLongClickMessage: (Message) -> Unit
)

data class GroupProfilePageViewState(
    val groupProfile: GroupProfile,
    val memberList: List<GroupMemberProfile>
)

data class GroupProfilePageAction(
    val setAvatar: (String) -> Unit,
    val quitGroup: () -> Unit,
    val onClickMember: (GroupMemberProfile) -> Unit,
)

data class ProfileUpdatePageViewStata(val personProfile: PersonProfile)

data class ProfileUpdatePageAction(
    val uploadImage: suspend (media: MediaResource) -> String,
    val updateProfile: (faceUrl: String, nickname: String, signature: String) -> Unit
)