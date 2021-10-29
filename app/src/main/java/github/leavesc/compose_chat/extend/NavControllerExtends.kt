package github.leavesc.compose_chat.extend

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import github.leavesc.compose_chat.base.model.Chat
import github.leavesc.compose_chat.model.Screen

/**
 * @Author: leavesC
 * @Date: 2021/7/26 0:28
 * @Desc:
 * @Github：https://github.com/leavesC
 */
fun NavController.navigate(screen: Screen) {
    navigate(route = screen.route)
}

fun NavController.navigateWithBack(screen: Screen) {
    popBackStack()
    navigate(route = screen.route)
}

private fun NavController.navToChatScreen(chat: Chat) {
    navigate(route = Screen.ChatScreen.generateRoute(chat = chat)) {
        popUpTo(route = Screen.ChatScreen.route) {
            inclusive = true
        }
    }
}

fun NavController.navToC2CChatScreen(friendId: String) {
    navToChatScreen(chat = Chat.C2C(id = friendId))
}

fun NavController.navToGroupChatScreen(groupId: String) {
    navToChatScreen(chat = Chat.Group(id = groupId))
}

fun NavController.navToHomeScreen() {
    popBackStack(route = Screen.HomeScreen.route, inclusive = false)
}

fun NavBackStackEntry.getStringArgument(key: String): String {
    return arguments?.getString(key)
        ?: throw IllegalArgumentException()
}

fun NavBackStackEntry.getIntArgument(key: String): Int {
    return arguments?.getString(key)?.toInt()
        ?: throw IllegalArgumentException()
}