package github.leavesczy.compose_chat.ui.home

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
import github.leavesczy.compose_chat.common.SelectPictureContract
import github.leavesczy.compose_chat.logic.HomeViewModel
import github.leavesczy.compose_chat.ui.profile.ProfileScreen
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.utils.randomFaceUrl
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2022/1/9 0:34
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun UpdateProfileScreen() {
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
        contract = SelectPictureContract()
    ) { imageUri ->
        if (imageUri != null) {
            coroutineScope.launch {
                val imageUrl = homeViewModel.uploadImage(imageUri)
                if (imageUrl.isBlank()) {
                    showToast("头像上传失败")
                } else {
                    faceUrl = imageUrl
                    showToast("头像已上传")
                }
            }
        }
    }
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            ProfileScreen(
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
                    CommonButton(
                        text = "随机头像"
                    ) {
                        faceUrl = randomFaceUrl()
                    }
                    CommonButton(
                        text = "选择头像"
                    ) {
                        selectPictureLauncher.launch(Unit)
                    }
                    CommonButton(
                        text = "保存设置"
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