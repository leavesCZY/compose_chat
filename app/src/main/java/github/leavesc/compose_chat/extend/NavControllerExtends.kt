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
    navigate(route = screen.route) {
        popBackStack()
    }
}

fun NavBackStackEntry.getArgument(key: String): String {
    return arguments?.getString(key)
        ?: throw IllegalArgumentException()
}