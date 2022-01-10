package github.leavesc.compose_chat.ui.chat.top_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.extend.LocalNavHostController

/**
 * @Author: leavesC
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ChatScreenTopBar(
    title: String,
    onClickBackMenu: (() -> Unit)? = null,
    onClickMoreMenu: () -> Unit,
) {
    val textInputService = LocalTextInputService.current
    val navHostController = LocalNavHostController.current
    val ime = LocalWindowInsets.current.ime
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                text = title,
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Localized description"
                )
            }, onClick = {
                if (onClickBackMenu != null) {
                    onClickBackMenu.invoke()
                } else {
                    if (ime.isVisible && textInputService != null) {
                        textInputService.hideSoftwareKeyboard()
                    } else {
                        navHostController.popBackStack()
                    }
                }
            })
        },
        actions = {
            IconButton(content = {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Localized description"
                )
            }, onClick = {
                onClickMoreMenu()
            })
        }
    )
}