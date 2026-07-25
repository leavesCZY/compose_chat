package github.leavesczy.compose_chat.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.BuildConfig
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.extensions.clickableNoRipple
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.theme.AppThemeMode.Dark
import github.leavesczy.compose_chat.theme.AppThemeMode.Gray
import github.leavesczy.compose_chat.theme.AppThemeMode.Light
import github.leavesczy.compose_chat.ui.main.logic.MainPageDrawerViewState
import github.leavesczy.compose_chat.widgets.AnimateBouncyImage
import kotlinx.coroutines.launch

@Composable
fun MainPageDrawer(viewState: MainPageDrawerViewState) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = viewState.drawerState.isOpen) {
        coroutineScope.launch {
            viewState.drawerState.close()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.80f)
            .background(color = AppTheme.colorScheme.c_FFE3EDFF_FF0F1726.color)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AnimateBouncyImage(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 20.dp, top = 20.dp)
                    .size(size = 90.dp)
                    .clickableNoRipple {
                        viewState.onClickPreviewImage(viewState.personProfile.avatarUrl)
                    },
                key = viewState.drawerState.isOpen,
                model = viewState.personProfile.avatarUrl
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier,
                    text = viewState.personProfile.id,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    color = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
                )
                Text(
                    modifier = Modifier,
                    text = viewState.personProfile.nickname,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color
                )
                Text(
                    modifier = Modifier,
                    text = viewState.personProfile.signature,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(space = 6.dp)
            ) {
                SelectableItem(
                    text = stringResource(id = R.string.profile),
                    icon = Icons.Filled.Cabin,
                    onClick = viewState.onClickUpdateProfile
                )
                val themeName = when (viewState.appTheme) {
                    Light -> R.string.theme_light
                    Dark -> R.string.theme_dark
                    Gray -> R.string.theme_gray
                }
                SelectableItem(
                    text = stringResource(id = themeName),
                    icon = Icons.Filled.Sailing,
                    onClick = viewState.onClickSwitchTheme
                )
                SelectableItem(
                    text = stringResource(id = R.string.switch_account),
                    icon = Icons.Filled.ColorLens,
                    onClick = viewState.onClickLogout
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
            )
            Copyright(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
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
            .height(height = 55.dp)
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(size = 22.dp),
            imageVector = icon,
            tint = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = text,
            fontSize = 17.sp,
            lineHeight = 18.sp,
            color = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
        )
    }
}

@Composable
private fun Copyright(modifier: Modifier) {
    val copyright = stringResource(
        id = R.string.copyright,
        BuildConfig.VERSION_CODE,
        BuildConfig.VERSION_NAME,
        BuildConfig.BUILD_TIME
    )
    Text(
        modifier = modifier,
        text = copyright,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        textAlign = TextAlign.Center,
        color = AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
    )
}