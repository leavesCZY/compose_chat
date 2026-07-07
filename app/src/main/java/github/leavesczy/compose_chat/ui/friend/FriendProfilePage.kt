package github.leavesczy.compose_chat.ui.friend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.friend.logic.DeleteFriendDialogViewState
import github.leavesczy.compose_chat.ui.friend.logic.FriendProfilePageViewState
import github.leavesczy.compose_chat.ui.friend.logic.SetFriendRemarkDialogViewState
import github.leavesczy.compose_chat.widgets.AnimatedBottomSheetDialog
import github.leavesczy.compose_chat.widgets.CommonButton
import github.leavesczy.compose_chat.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.widgets.ProfilePanel

@Composable
internal fun FriendProfilePage(pageViewState: FriendProfilePageViewState) {
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
        ) {
            val personProfile = pageViewState.personProfile
            if (personProfile != null) {
                val introduction = buildString {
                    append(stringResource(id = R.string.friend_profile_id, personProfile.id))
                    if (personProfile.remark.isNotBlank()) {
                        append(
                            stringResource(
                                id = R.string.friend_profile_remark,
                                personProfile.remark
                            )
                        )
                    }
                }
                ProfilePanel(
                    title = personProfile.nickname,
                    subtitle = personProfile.signature,
                    introduction = introduction,
                    avatarUrl = personProfile.avatarUrl
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                    ) {
                        if (!pageViewState.isMe) {
                            CommonButton(
                                text = stringResource(id = R.string.chat_now),
                                onClick = pageViewState.onClickChat
                            )
                            if (pageViewState.isFriend) {
                                CommonButton(
                                    text = stringResource(id = R.string.set_remark),
                                    onClick = pageViewState.onClickSetFriendRemark
                                )
                                CommonButton(
                                    text = stringResource(id = R.string.delete_friend),
                                    onClick = pageViewState.onClickDeleteFriend
                                )
                            } else {
                                CommonButton(
                                    text = stringResource(id = R.string.add_as_friend),
                                    onClick = pageViewState.onClickAddFriend
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DeleteFriendDialog(viewState: DeleteFriendDialogViewState) {
    if (viewState.isVisible) {
        AlertDialog(
            modifier = Modifier,
            containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF22202A.color,
            onDismissRequest = viewState.onDismissDialog,
            text = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.confirm_delete_friend),
                    fontSize = 17.sp,
                    lineHeight = 18.sp,
                    color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
                )
            },
            confirmButton = {
                TextButton(
                    onClick = viewState.onDismissDialog
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.cancel),
                        fontSize = 15.sp,
                        lineHeight = 16.sp,
                        color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewState.onDeleteFriend
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.delete),
                        fontSize = 15.sp,
                        lineHeight = 16.sp,
                        color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
                    )
                }
            }
        )
    }
}

@Composable
internal fun SetFriendRemarkDialog(viewState: SetFriendRemarkDialogViewState) {
    AnimatedBottomSheetDialog(
        modifier = Modifier,
        visible = viewState.isVisible,
        onDismissRequest = viewState.onDismissDialog
    ) {
        var remark by remember(key1 = viewState.isVisible) {
            mutableStateOf(value = viewState.remark)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.87f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 24.dp)
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp),
                value = remark,
                onValueChange = { value ->
                    remark = value
                },
                label = stringResource(id = R.string.input_remark)
            )
            CommonButton(text = stringResource(id = R.string.set_remark)) {
                viewState.onSetFriendRemark(remark)
            }
        }
    }
}