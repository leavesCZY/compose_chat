package github.leavesczy.compose_chat.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.DrawerState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import github.leavesczy.compose_chat.base.model.*
import github.leavesczy.compose_chat.extend.getIntArgument
import github.leavesczy.compose_chat.extend.getStringArgument
import github.leavesczy.compose_chat.utils.StringUtils

/**
 * @Author: leavesCZY
 * @Date: 2021/7/6 15:17
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class Page(val route: String) {

    private companion object {
        private const val loginPage = "loginPage"
        private const val homePage = "homePage"

        private const val friendProfilePage = "friendProfilePage"
        private const val keyFriendId = "keyFriendId"

        private const val groupProfilePage = "groupProfilePage"
        private const val keyGroupId = "keyGroupId"

        private const val chatPage = "chatPage"
        private const val keyChatPagePartyType = "keyChatPagePartyType"
        private const val keyChatPagePartyId = "keyChatPagePartyId"

        private const val previewImagePage = "previewImagePage"
        private const val keyPreviewImagePageImagePath = "keyPreviewImagePageImagePath"

        private const val updateProfilePage = "updateProfilePage"

        private fun formatArgument(argument: String): String {
            return try {
                StringUtils.str2HexStr(argument)
            } catch (e: Throwable) {
                e.printStackTrace()
                argument
            }
        }

        private fun decodeArgument(argument: String): String {
            return try {
                StringUtils.hexStr2Str(argument)
            } catch (e: Throwable) {
                e.printStackTrace()
                argument
            }
        }
    }

    object LoginPage : Page(route = loginPage)

    object HomePage : Page(route = homePage)

    object FriendProfilePage : Page(route = friendProfilePage + "/{${keyFriendId}}") {

        fun generateRoute(friendId: String): String {
            return friendProfilePage + "/${formatArgument(argument = friendId)}"
        }

        fun getArgument(entry: NavBackStackEntry): String {
            return decodeArgument(entry.getStringArgument(key = keyFriendId))
        }

    }

    object GroupProfilePage : Page(route = groupProfilePage + "/{${keyGroupId}}") {

        fun generateRoute(groupId: String): String {
            return groupProfilePage + "/${formatArgument(argument = groupId)}"
        }

        fun getArgument(entry: NavBackStackEntry): String {
            return decodeArgument(entry.getStringArgument(key = keyGroupId))
        }

    }

    object ChatPage :
        Page(route = chatPage + "/{${keyChatPagePartyType}}" + "/{${keyChatPagePartyId}}") {

        fun generateRoute(chat: Chat): String {
            return chatPage + "/${chat.type}" + "/${formatArgument(argument = chat.id)}"
        }

        fun getArgument(entry: NavBackStackEntry): Chat {
            val type = entry.getIntArgument(key = keyChatPagePartyType)
            val id = decodeArgument(entry.getStringArgument(key = keyChatPagePartyId))
            return Chat.find(type = type, id = id)
        }

    }

    object PreviewImagePage :
        Page(route = previewImagePage + "/{${keyPreviewImagePageImagePath}}") {

        fun generateRoute(imagePath: String): String {
            return previewImagePage + "/${formatArgument(argument = imagePath)}"
        }

        fun getArgument(entry: NavBackStackEntry): String {
            return decodeArgument(entry.getStringArgument(key = keyPreviewImagePageImagePath))
        }

    }

    object UpdateProfilePage : Page(route = updateProfilePage) {

        fun generateRoute(): String {
            return updateProfilePage
        }

    }

}

data class LoginPageState(
    val showLogo: Boolean,
    val showInput: Boolean,
    val showLoading: Boolean,
    val loginSuccess: Boolean,
    val lastLoginUserId: String
)

enum class HomePageTab(
    val icon: ImageVector
) {
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

data class HomePageDrawerState(
    val drawerState: DrawerState,
    val appTheme: AppTheme,
    val userProfile: PersonProfile,
    val switchToNextTheme: () -> Unit,
    val logout: () -> Unit
)

data class HomePageTopBarState(
    val pageSelected: HomePageTab,
    val openDrawer: () -> Unit,
    val onAddFriend: () -> Unit,
    val onJoinGroup: () -> Unit,
)

data class HomePageBottomBarState(
    val tabList: List<HomePageTab>,
    val tabSelected: HomePageTab,
    val onTabSelected: (HomePageTab) -> Unit,
    val unreadMessageCount: Long
)

data class HomePageFriendshipPanelState(
    val modalBottomSheetState: ModalBottomSheetState,
    val addFriend: (userId: String) -> Unit,
    val joinGroup: (groupId: String) -> Unit
)

data class ConversationPageState(
    val listState: LazyListState,
    val conversationList: List<Conversation>,
    val onClickConversation: (Conversation) -> Unit,
    val onDeleteConversation: (Conversation) -> Unit,
    val onPinnedConversation: (Conversation, Boolean) -> Unit
)

data class FriendshipPageState(
    val listState: LazyListState,
    val joinedGroupList: List<GroupProfile>,
    val friendList: List<PersonProfile>,
    val onClickGroup: (GroupProfile) -> Unit,
    val onClickFriend: (PersonProfile) -> Unit
)

data class FriendProfilePageState(
    val personProfile: PersonProfile,
    val showAlterBtb: Boolean,
    val showAddBtn: Boolean
)

data class PersonProfilePageState(
    val personProfile: PersonProfile
)

data class ChatPageState(
    val messageList: List<Message>,
    val mushScrollToBottom: Boolean,
    val showLoadMore: Boolean,
    val loadFinish: Boolean
)

data class GroupProfilePageState(
    val groupProfile: GroupProfile,
    val memberList: List<GroupMemberProfile>
)