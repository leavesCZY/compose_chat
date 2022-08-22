package github.leavesczy.compose_chat.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.model.ProfileUpdatePageAction
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.main.logic.ProfileUpdateViewModel
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: CZY
 * @Date: 2022/7/17 14:05
 * @Desc:
 */
class ProfileUpdateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val profileUpdateViewModel = viewModel<ProfileUpdateViewModel>()
            val profileUpdatePageViewStata by profileUpdateViewModel.profileUpdatePageViewStata.collectAsState()
            val profileUpdatePageAction = remember {
                ProfileUpdatePageAction(uploadImage = { media ->
                    profileUpdateViewModel.uploadImage(mediaResource = media)
                }, updateProfile = { faceUrl, nickname, signature ->
                    profileUpdateViewModel.updateProfile(
                        faceUrl = faceUrl,
                        nickname = nickname,
                        signature = signature
                    )
                })
            }
            ComposeChatTheme {
                ProfileUpdatePage(
                    viewStata = profileUpdatePageViewStata,
                    profileUpdatePageAction = profileUpdatePageAction
                )
            }
        }
    }

}