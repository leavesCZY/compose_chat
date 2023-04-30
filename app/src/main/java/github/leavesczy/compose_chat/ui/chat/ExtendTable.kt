package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ExtendTable(launchImagePicker: () -> Unit, launchTakePicture: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp)
    ) {
        Function(
            text = "相册",
            icon = Icons.Filled.InsertPhoto,
            onClick = launchImagePicker
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(width = 10.dp)
        )
        Function(
            text = "拍摄",
            icon = Icons.Filled.PhotoCamera,
            onClick = launchTakePicture
        )
    }
}

@Composable
private fun Function(text: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(
                horizontal = 10.dp, vertical = 10.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(size = 42.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}
