package github.leavesczy.compose_chat.ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.model.ProfileUpdatePageAction
import github.leavesczy.compose_chat.model.ProfileUpdatePageViewStata
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel
import github.leavesczy.compose_chat.utils.randomFaceUrl
import github.leavesczy.compose_chat.utils.showToast
import github.leavesczy.matisse.*
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2022/1/9 0:34
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ProfileUpdatePage(
    viewStata: ProfileUpdatePageViewStata,
    profileUpdatePageAction: ProfileUpdatePageAction
) {
    val realtimePersonProfile = viewStata.personProfile
    var faceUrl by remember(key1 = realtimePersonProfile) {
        mutableStateOf(realtimePersonProfile.faceUrl)
    }
    var nickname by remember(key1 = realtimePersonProfile) {
        mutableStateOf(realtimePersonProfile.nickname)
    }
    var signature by remember(key1 = realtimePersonProfile) {
        mutableStateOf(realtimePersonProfile.signature)
    }
    val coroutineScope = rememberCoroutineScope()
    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = MatisseContract()
    ) { result ->
        if (result.isNotEmpty()) {
            coroutineScope.launch {
                val imageUrl = profileUpdatePageAction.uploadImage(result[0])
                if (imageUrl.isBlank()) {
                    showToast("图片上传失败")
                } else {
                    faceUrl = imageUrl
                    showToast("图片已上传")
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .verticalScroll(state = rememberScrollState())
        ) {
            ProfilePanel(
                title = nickname,
                subtitle = signature,
                introduction = "",
                avatarUrl = faceUrl
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 5.dp
                            ),
                        value = nickname,
                        onValueChange = {
                            if (it.length > 16) {
                                return@CommonOutlinedTextField
                            }
                            nickname = it
                        },
                        label = "nickname"
                    )
                    CommonOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 5.dp
                            ),
                        value = signature,
                        onValueChange = {
                            if (it.length > 50) {
                                return@CommonOutlinedTextField
                            }
                            signature = it
                        },
                        label = "signature"
                    )
                    CommonButton(text = "随机图片") {
                        faceUrl = randomFaceUrl()
                    }
                    CommonButton(text = "本地图片") {
                        val matisse = Matisse(
                            theme = if (AppThemeCache.currentTheme == AppTheme.Dark) {
                                DarkMatisseTheme
                            } else {
                                LightMatisseTheme
                            },
                            maxSelectable = 1,
                            captureStrategy = MediaStoreCaptureStrategy()
                        )
                        selectPictureLauncher.launch(matisse)
                    }
                    CommonButton(text = "确认修改") {
                        profileUpdatePageAction.updateProfile(faceUrl, nickname, signature)
                    }
                }
            }
        }
    }
}