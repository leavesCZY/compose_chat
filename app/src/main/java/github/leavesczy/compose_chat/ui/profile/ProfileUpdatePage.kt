package github.leavesczy.compose_chat.ui.profile

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.ui.profile.logic.ProfileUpdatePageViewStata
import github.leavesczy.compose_chat.ui.theme.AppTheme
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Composable
internal fun ProfileUpdatePage(pageViewStata: ProfileUpdatePageViewStata) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(bottom = 30.dp)
        ) {
            val personProfile = pageViewStata.personProfile
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
                                pageViewStata.onNicknameChanged(nickname)
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
                                pageViewStata.onSignatureChanged(signature)
                            }
                        )
                        CommonButton(
                            text = stringResource(id = R.string.random_avatar),
                            onClick = pageViewStata.setRandomAvatar
                        )
                        CommonButton(
                            text = stringResource(id = R.string.confirm_update),
                            onClick = pageViewStata.onConfirmUpdate
                        )
                    }
                }
            }
        }
    }
}