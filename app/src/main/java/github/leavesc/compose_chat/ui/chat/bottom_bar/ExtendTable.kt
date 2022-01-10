package github.leavesc.compose_chat.ui.chat.bottom_bar

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import github.leavesc.compose_chat.R

/**
 * @Author: leavesC
 * @Date: 2022/1/1 12:10
 * @Desc:
 */
@Composable
fun ExtendTable(
    selectPictureLauncher: ManagedActivityResultLauncher<Unit, Uri?>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 180.dp)
    ) {
        IconButton(onClick = {
            selectPictureLauncher.launch(Unit)
        }) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(size = 40.dp)
                        .padding(bottom = 6.dp),
                    painter = painterResource(id = R.drawable.icon_album),
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
                    contentDescription = null
                )
                Text(
                    text = "发送图片",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1.copy(fontSize = 13.sp)
                )
            }
        }
    }
}

