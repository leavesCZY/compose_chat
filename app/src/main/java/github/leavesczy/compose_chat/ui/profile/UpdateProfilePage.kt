package github.leavesczy.compose_chat.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.logic.HomeViewModel
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
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
fun UpdateProfilePage() {
    val homeViewModel = viewModel<HomeViewModel>()
    val userProfile by homeViewModel.personProfile.collectAsState()
    var faceUrl by rememberSaveable {
        mutableStateOf(
            userProfile.faceUrl
        )
    }
    var nickname by rememberSaveable {
        mutableStateOf(
            userProfile.nickname
        )
    }
    var signature by rememberSaveable {
        mutableStateOf(
            userProfile.signature
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = MatisseContract()
    ) { result ->
        if (result.isNotEmpty()) {
            coroutineScope.launch {
                val imageUrl = homeViewModel.uploadImage(image = result[0])
                if (imageUrl.isBlank()) {
                    showToast("图片上传失败")
                } else {
                    faceUrl = imageUrl
                    showToast("图片已上传")
                }
            }
        }
    }
    Scaffold { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            ProfilePanel(
                title = nickname,
                subtitle = signature,
                introduction = "",
                avatarUrl = faceUrl,
                contentPadding = contentPadding
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
                    CommonButton(
                        text = "随机图片"
                    ) {
                        faceUrl = randomFaceUrl()
                    }
                    CommonButton(
                        text = "本地图片"
                    ) {
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
                    CommonButton(
                        text = "确认修改"
                    ) {
                        homeViewModel.updateProfile(
                            faceUrl,
                            nickname,
                            signature
                        )
                    }
                }
            }
        }
    }
}