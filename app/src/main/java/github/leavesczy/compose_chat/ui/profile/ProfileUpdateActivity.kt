package github.leavesczy.compose_chat.ui.profile

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.base.BaseActivity
import github.leavesczy.compose_chat.ui.profile.logic.ProfileUpdateViewModel

class ProfileUpdateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val profileUpdateViewModel = viewModel<ProfileUpdateViewModel>()
            ProfileUpdatePage(pageViewState = profileUpdateViewModel.pageViewState)
        }
    }

}