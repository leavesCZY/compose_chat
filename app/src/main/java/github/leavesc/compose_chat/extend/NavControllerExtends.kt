package github.leavesc.compose_chat.extend

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
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

fun NavController.navToChatFriendScreen(friendId: String) {
    navigate(route = Screen.ChatFriendScreen(friendId = friendId).route) {
        popUpTo(route = Screen.ChatFriendScreen().route) {
            inclusive = true
        }
    }
}

fun NavController.navToHomeScreen() {
    popBackStack(route = Screen.HomeScreen.route, inclusive = false)
}

fun NavBackStackEntry.getArgument(key: String): String {
    return arguments?.getString(key)
        ?: throw IllegalArgumentException()
}