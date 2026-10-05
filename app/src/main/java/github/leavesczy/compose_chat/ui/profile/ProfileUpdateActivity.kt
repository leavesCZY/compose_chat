package github.leavesczy.compose_chat.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.profile.logic.ProfileUpdateViewModel
import github.leavesczy.compose_chat.widgets.LoadingDialog

class ProfileUpdateActivity : BaseActivity() {

    private val profileUpdateViewModel by viewModels<ProfileUpdateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileUpdatePage(pageViewState = profileUpdateViewModel.pageViewState)
            LoadingDialog(viewState = profileUpdateViewModel.loadingDialogViewState)
        }
    }

}
