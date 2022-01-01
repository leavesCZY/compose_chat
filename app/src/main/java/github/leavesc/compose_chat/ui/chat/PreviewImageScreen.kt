package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import github.leavesc.compose_chat.ui.weigets.CoilImage

/**
 * @Author: leavesC
 * @Date: 2022/1/1 17:45
 * @Desc:
 */
@Composable
fun PreviewImageScreen(
    navController: NavHostController,
    imagePath: String
) {
    Scaffold(
        modifier = Modifier,
        backgroundColor = MaterialTheme.colors.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth(),
                data = imagePath
            )
        }
    }
}