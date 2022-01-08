package github.leavesc.compose_chat.model

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.DrawerState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.extend.getIntArgument
import github.leavesc.compose_chat.extend.getStringArgument
import github.leavesc.compose_chat.utils.StringUtils

/**
 * @Author: leavesC
 * @Date: 2021/7/6 15:17
 * @Desc:
 * @Github：https://github.com/leavesC
 */
sealed class Screen(val route: String) {

    private companion object {
        private const val loginScreen = "loginScreen"
        private const val homeScreen = "homeScreen"

        private const val friendProfileScreen = "friendProfileScreen"
        private const val keyFriendId = "keyFriendId"

        private const val groupProfileScreen = "groupProfileScreen"
        private const val keyGroupId = "keyGroupId"

        private const val chatScreen = "chatScreen"
        private const val keyChatScreenPartyType = "keyChatScreenPartyType"
        private const val keyChatScreenPartyId = "keyChatScreenPartyId"

        private const val previewImageScreen = "previewImageScreen"
        private const val keyPreviewImageScreenPartyType = "keyPreviewImageScreenImagePath"

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

    object LoginScreen : Screen(route = loginScreen)

    object HomeScreen : Screen(route = homeScreen)

    object FriendProfileScreen : Screen(route = friendProfileScreen + "/{${keyFriendId}}") {

        fun generateRoute(friendId: String): String {
            return friendProfileScreen + "/${formatArgument(argument = friendId)}"
        }

        fun getArgument(entry: NavBackStackEntry): String {
            return decodeArgument(entry.getStringArgument(key = keyFriendId))
        }

    }

    object GroupProfileScreen : Screen(route = groupProfileScreen + "/{${keyGroupId}}") {

        fun generateRoute(groupId: String): String {
            return groupProfileScreen + "/${formatArgument(argument = groupId)}"
        }

        fun getArgument(entry: NavBackStackEntry): String {
            return decodeArgument(entry.getStringArgument(key = keyGroupId))
        }

    }

    object ChatScreen :
        Screen(route = chatScreen + "/{${keyChatScreenPartyType}}" + "/{${keyChatScreenPartyId}}") {

        fun generateRoute(chat: Chat): String {
            return chatScreen + "/${chat.type}" + "/${formatArgument(argument = chat.id)}"
        }

        fun getArgument(entry: NavBackStackEntry): Chat {
            val type = entry.getIntArgument(key = keyChatScreenPartyType)
            val id = decodeArgument(entry.getStringArgument(key = keyChatScreenPartyId))
            return Chat.find(type = type, id = id)
        }

    }

    object PreviewImageScreen :
        Screen(route = previewImageScreen + "/{${keyPreviewImageScreenPartyType}}") {

        fun generateRoute(imagePath: String): String {
            return previewImageScreen + "/${formatArgument(argument = imagePath)}"
        }

        fun getArgument(entry: NavBackStackEntry): String {
            return decodeArgument(entry.getStringArgument(key = keyPreviewImageScreenPartyType))
        }

    }

}

data class LoginScreenState(
    val showLogo: Boolean,
    val showInput: Boolean,
    val showLoading: Boolean,
    val loginSuccess: Boolean,
    val lastLoginUserId: String
)

enum class HomeScreenTab(
    val icon: ImageVector
) {
    Conversation(
        icon = Icons.Filled.Cabin
    ),
    Friendship(
        icon = Icons.Filled.Sailing,
    ),
    Person(
        icon = Icons.Filled.TheaterComedy,
    );
}

data class HomeScreenDrawerState(
    val drawerState: DrawerState,
    val appTheme: AppTheme,
    val userProfile: PersonProfile,
    val switchToNextTheme: () -> Unit,
    val updateProfile: (faceUrl: String, nickname: String, signature: String) -> Unit,
    val uploadImage: suspend (Uri) -> String,
    val logout: () -> Unit,
)

data class HomeScreenTopBarState(
    val screenSelected: HomeScreenTab,
    val openDrawer: () -> Unit,
    val onAddFriend: () -> Unit,
    val onJoinGroup: () -> Unit,
)

data class HomeScreenBottomBarState(
    val homeScreenList: List<HomeScreenTab>,
    val homeScreenSelected: HomeScreenTab,
    val unreadMessageCount: Long,
    val onHomeScreenTabSelected: (HomeScreenTab) -> Unit
)

data class HomeScreenSheetContentState(
    val modalBottomSheetState: ModalBottomSheetState,
    val toAddFriend: (userId: String) -> Unit,
    val toJoinGroup: (groupId: String) -> Unit
)

data class ConversationScreenState(
    val listState: LazyListState,
    val conversationList: List<Conversation>,
    val onClickConversation: (Conversation) -> Unit,
    val onDeleteConversation: (Conversation) -> Unit,
    val onPinnedConversation: (Conversation, Boolean) -> Unit
)

data class FriendshipScreenState(
    val listState: LazyListState,
    val joinedGroupList: List<GroupProfile>,
    val friendList: List<PersonProfile>,
    val onClickGroup: (GroupProfile) -> Unit,
    val onClickFriend: (PersonProfile) -> Unit
)

data class PersonProfileScreenState(
    val personProfile: PersonProfile
)

data class ChatScreenState(
    val messageList: List<Message>,
    val mushScrollToBottom: Boolean,
    val showLoadMore: Boolean,
    val loadFinish: Boolean
)

data class GroupProfileScreenState(
    val groupProfile: GroupProfile,
    val memberList: List<GroupMemberProfile>
)