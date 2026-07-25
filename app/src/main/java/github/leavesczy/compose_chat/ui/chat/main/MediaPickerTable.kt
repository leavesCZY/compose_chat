package github.leavesczy.compose_chat.ui.chat.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.extensions.clickableNoRipple
import github.leavesczy.compose_chat.theme.AppTheme

@Composable
fun MediaPickerTable(
    modifier: Modifier,
    onClickImagePicker: () -> Unit,
    onClickTakePicture: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.Start
        ),
        verticalAlignment = Alignment.Top
    ) {
        MediaPickerAction(
            modifier = Modifier,
            text = stringResource(id = R.string.take_photo),
            icon = Icons.Filled.PhotoCamera,
            onClick = onClickTakePicture
        )
        MediaPickerAction(
            modifier = Modifier,
            text = stringResource(id = R.string.album),
            icon = Icons.Filled.InsertPhoto,
            onClick = onClickImagePicker
        )
    }
}

@Composable
private fun MediaPickerAction(
    modifier: Modifier,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            modifier = Modifier
                .size(size = 42.dp),
            imageVector = icon,
            tint = AppTheme.colorScheme.c_FF5BA3F7_FF60A5FA.color,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .padding(top = 4.dp),
            text = text,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            color = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
        )
    }
}
