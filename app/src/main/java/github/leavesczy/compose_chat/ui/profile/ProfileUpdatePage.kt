package github.leavesczy.compose_chat.ui.profile

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.profile.logic.ProfileUpdatePageViewState
import github.leavesczy.compose_chat.widgets.CommonButton
import github.leavesczy.compose_chat.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.widgets.ProfilePanel

@Composable
internal fun ProfileUpdatePage(pageViewState: ProfileUpdatePageViewState) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.c_FFEAF2FF_FF0B1220.color,
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(bottom = 30.dp)
            ) {
                val personProfile = pageViewState.personProfile
                if (personProfile != null) {
                    ProfilePanel(
                        title = personProfile.nickname,
                        subtitle = personProfile.signature,
                        introduction = "",
                        avatarUrl = personProfile.avatarUrl
                    ) {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                        ) {
                            CommonOutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                label = stringResource(id = R.string.nickname_label),
                                value = personProfile.nickname,
                                onValueChange = { nickname ->
                                    if (nickname.length > 16) {
                                        return@CommonOutlinedTextField
                                    }
                                    pageViewState.onNicknameChanged(nickname)
                                }
                            )
                            CommonOutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                label = stringResource(id = R.string.signature_label),
                                value = personProfile.signature,
                                onValueChange = { signature ->
                                    if (signature.length > 60) {
                                        return@CommonOutlinedTextField
                                    }
                                    pageViewState.onSignatureChanged(signature)
                                }
                            )
                            CommonButton(
                                text = stringResource(id = R.string.random_avatar),
                                onClick = pageViewState.onClickSetRandomAvatar
                            )
                            CommonButton(
                                text = stringResource(id = R.string.confirm_update),
                                onClick = pageViewState.onConfirmUpdate
                            )
                        }
                    }
                } else if (pageViewState.loadFailed) {
                    Text(
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .padding(horizontal = 24.dp),
                        text = stringResource(id = R.string.profile_load_failed),
                        fontSize = 16.sp,
                        color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .statusBarsPadding(),
                onClick = {
                    onBackPressedDispatcher?.onBackPressed()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = stringResource(id = R.string.cd_back),
                    tint = if (pageViewState.personProfile != null) {
                        AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
                    } else {
                        AppTheme.colorScheme.c_FF0B1F3A_DEFFFFFF.color
                    }
                )
            }
        }
    }
}
