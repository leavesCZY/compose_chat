package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.common.SelectPictureContract
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.logic.HomeViewModel
import github.leavesc.compose_chat.ui.weigets.CircleImage
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import github.leavesc.compose_chat.utils.randomFaceUrl
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2022/1/9 0:34
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun UpdateProfileScreen() {
    val navHostController = LocalNavHostController.current
    val homeViewModel = viewModel<HomeViewModel>()
    val userProfile by homeViewModel.personProfile.collectAsState()
    var faceUrl by remember(key1 = userProfile) {
        mutableStateOf(
            userProfile.faceUrl
        )
    }
    var nickname by remember(key1 = userProfile) {
        mutableStateOf(
            userProfile.nickname
        )
    }
    var signature by remember(key1 = userProfile) {
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
    Scaffold(modifier = Modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(content = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }, onClick = {
                        navHostController.popBackStack()
                    })
                },
                title = {
                    Text(
                        text = "个人资料",
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        }) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(
                    top = 0.dp,
                    bottom = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircleImage(
                modifier = Modifier.size(size = 140.dp),
                data = faceUrl
            )
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 120.dp)
                    .padding(
                        horizontal = 20.dp,
                        vertical = 5.dp
                    ),
                value = faceUrl,
                onValueChange = { faceUrl = it },
                label = "faceUrl",
            )
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
                modifier = Modifier.fillMaxWidth(),
                text = "选择头像"
            ) {
                selectPictureLauncher.launch(Unit)
            }
            CommonButton(
                modifier = Modifier.fillMaxWidth(),
                text = "随机头像"
            ) {
                faceUrl = randomFaceUrl()
            }
            CommonButton(
                modifier = Modifier.fillMaxWidth(),
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