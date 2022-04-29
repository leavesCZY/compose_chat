package github.leavesczy.compose_chat.extend

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.model.Page

/**
 * @Author: leavesCZY
 * @Date: 2021/7/26 0:28
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
fun NavController.navigate(page: Page) {
    navigate(route = page.route)
}

fun NavController.navToLogin() {
    navigate(route = Page.LoginPage.route) {
        popUpTo(route = Page.HomePage.route) {
            inclusive = true
        }
    }
}

fun NavController.navigateWithBack(page: Page) {
    popBackStack()
    navigate(route = page.route)
}

private fun NavController.navToChatPage(chat: Chat) {
    navigate(route = Page.ChatPage.generateRoute(chat = chat)) {
        popUpTo(route = Page.ChatPage.route) {
            inclusive = true
        }
    }
}

fun NavController.navToC2CChatPage(friendId: String) {
    navToChatPage(chat = Chat.C2C(id = friendId))
}

fun NavController.navToGroupChatPage(groupId: String) {
    navToChatPage(chat = Chat.Group(id = groupId))
}

fun NavController.navToHomePage() {
    popBackStack(route = Page.HomePage.route, inclusive = false)
}

fun NavController.navToPreviewImagePage(imagePath: String) {
    navigate(route = Page.PreviewImagePage.generateRoute(imagePath = imagePath))
}

fun NavBackStackEntry.getStringArgument(key: String): String {
    return arguments?.getString(key)
        ?: throw IllegalArgumentException()
}

fun NavBackStackEntry.getIntArgument(key: String): Int {
    return arguments?.getString(key)?.toInt()
        ?: throw IllegalArgumentException()
}