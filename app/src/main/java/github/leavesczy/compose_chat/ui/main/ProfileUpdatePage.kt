package github.leavesczy.compose_chat.ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.ui.main.logic.ProfileUpdateViewModel
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel
import github.leavesczy.compose_chat.utils.randomFaceUrl
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaStoreCaptureStrategy

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ProfileUpdatePage(profileUpdateViewModel: ProfileUpdateViewModel) {
    val profileUpdatePageViewStata = profileUpdateViewModel.profileUpdatePageViewStata
    if (profileUpdatePageViewStata != null) {
        val personProfile = profileUpdatePageViewStata.personProfile
        val selectPictureLauncher = rememberLauncherForActivityResult(
            contract = MatisseContract()
        ) { result ->
            if (result.isNotEmpty()) {
                profileUpdateViewModel.onAvatarUrlChanged(mediaResource = result[0])
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
                    title = personProfile.nickname,
                    subtitle = personProfile.signature,
                    introduction = "",
                    avatarUrl = personProfile.faceUrl
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CommonOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp, vertical = 5.dp
                                ),
                            value = personProfile.nickname,
                            onValueChange = {
                                if (it.length > 16) {
                                    return@CommonOutlinedTextField
                                }
                                profileUpdateViewModel.onNicknameChanged(nickname = it)
                            },
                            label = "nickname"
                        )
                        CommonOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            value = personProfile.signature,
                            onValueChange = {
                                if (it.length > 40) {
                                    return@CommonOutlinedTextField
                                }
                                profileUpdateViewModel.onSignatureChanged(signature = it)
                            },
                            label = "signature"
                        )
                        CommonButton(text = "随机图片") {
                            profileUpdateViewModel.onAvatarUrlChanged(imageUrl = randomFaceUrl())
                        }
                        CommonButton(text = "本地图片") {
                            val matisse = Matisse(
                                maxSelectable = 1, captureStrategy = MediaStoreCaptureStrategy()
                            )
                            selectPictureLauncher.launch(matisse)
                        }
                        CommonButton(text = "确认修改") {
                            profileUpdateViewModel.confirmUpdate()
                        }
                    }
                }
            }
        }
    }
}