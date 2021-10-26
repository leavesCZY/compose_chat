package github.leavesc.compose_chat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import github.leavesc.compose_chat.base.model.Message
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.extend.getArgument

/**
 * @Author: leavesC
 * @Date: 2021/7/6 15:17
 * @Desc:
 * @Github：https://github.com/leavesC
 */
sealed class Screen(val route: String) {

    private companion object {
        private const val keyUserId = "keyUserId"
        private const val loginScreen = "loginScreen"
        private const val homeScreen = "homeScreen"
        private const val baseFriendProfileScreen = "friendProfileScreen"
        private const val baseChatScreen = "chatScreen"
    }

    object LoginScreen : Screen(route = loginScreen)

    object HomeScreen : Screen(route = homeScreen)

    class FriendProfileScreen(friendId: String = "") :
        Screen(route = generateRoute(friendId = friendId)) {

        companion object {

            private fun generateRoute(friendId: String): String {
                return baseFriendProfileScreen + if (friendId.isBlank()) {
                    "/{${keyUserId}}"
                } else {
                    "/${friendId}"
                }
            }

            fun getArgument(entry: NavBackStackEntry): String {
                return entry.getArgument(key = keyUserId)
            }

        }

    }

    class ChatScreen(friendId: String = "") :
        Screen(route = generateRoute(friendId = friendId)) {

        companion object {

            private fun generateRoute(friendId: String): String {
                return baseChatScreen + if (friendId.isBlank()) {
                    "/{${keyUserId}}"
                } else {
                    "/${friendId}"
                }
            }

            fun getArgument(entry: NavBackStackEntry): String {
                return entry.getArgument(key = keyUserId)
            }

        }

    }

}

enum class HomeScreenTab(
    val icon: ImageVector
) {
    Conversation(
        icon = Icons.Filled.Favorite
    ),
    Friendship(
        icon = Icons.Filled.Album,
    ),
    PersonProfile(
        icon = Icons.Filled.Flare,
    );
}

class ChatScreenState(
    val showLoadMore: Boolean,
    val loadFinish: Boolean,
    val messageList: List<Message>,
)

data class HomeDrawerViewState(
    val appTheme: AppTheme,
    val userProfile: PersonProfile,
    val switchToNextTheme: () -> Unit,
    val updateProfile: (faceUrl: String, nickname: String, signature: String) -> Unit,
    val logout: () -> Unit,
)

data class LoginScreenState(
    val showLogo: Boolean,
    val showInput: Boolean,
    val showLoading: Boolean,
    val loginSuccess: Boolean,
    val lastLoginUserId: String = ""
)