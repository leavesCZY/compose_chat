package github.leavesczy.compose_chat.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.BuildConfig
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.logic.MainPageDrawerViewState
import github.leavesczy.compose_chat.ui.widgets.ComponentImage
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageDrawer(viewState: MainPageDrawerViewState) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = viewState.drawerState.isOpen) {
        coroutineScope.launch {
            viewState.drawerState.close()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.85f)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 22.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val personProfile by viewState.personProfile
            val padding = 20.dp
            ComponentImage(
                modifier = Modifier
                    .padding(start = padding)
                    .size(size = 90.dp)
                    .clip(shape = CircleShape)
                    .clickableNoRipple {
                        viewState.previewImage(personProfile.faceUrl)
                    },
                model = personProfile.faceUrl
            )
            Text(
                modifier = Modifier.padding(
                    start = padding,
                    end = padding,
                    top = padding
                ),
                text = personProfile.id,
                fontSize = 20.sp
            )
            Text(
                modifier = Modifier.padding(horizontal = padding),
                text = personProfile.nickname,
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.padding(horizontal = padding),
                text = personProfile.signature,
                fontSize = 18.sp
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 30.dp)
            )
            SelectableItem(
                text = "个人资料",
                icon = Icons.Filled.Cabin,
                onClick = viewState.updateProfile
            )
            SelectableItem(
                text = "切换主题",
                icon = Icons.Filled.Sailing,
                onClick = viewState.switchTheme
            )
            SelectableItem(
                text = "切换账号",
                icon = Icons.Filled.ColorLens,
                onClick = viewState.logout
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                text = buildString {
                    append("公众号: 字节数组")
                    append("\n")
                    append("VersionCode: ")
                    append(BuildConfig.VERSION_CODE)
                    append("\n")
                    append("VersionName: ")
                    append(BuildConfig.VERSION_NAME)
                    append("\n")
                    append("BuildTime: ")
                    append(BuildConfig.BUILD_TIME)
                },
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun SelectableItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(height = 60.dp)
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(size = 22.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = text,
            fontSize = 17.sp
        )
    }
}