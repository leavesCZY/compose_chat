package github.leavesczy.compose_chat.ui.profile

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.profile.logic.ProfileUpdateViewModel

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ProfileUpdateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val profileUpdateViewModel = viewModel<ProfileUpdateViewModel>()
            ProfileUpdatePage(pageViewStata = profileUpdateViewModel.profileUpdatePageViewStata)
        }
    }

}