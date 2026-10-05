package github.leavesczy.compose_chat.ui.main.friendship

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.main.friendship.logic.FriendshipDialogViewState
import github.leavesczy.compose_chat.widgets.AnimatedBottomSheetDialog
import github.leavesczy.compose_chat.widgets.CommonButton
import github.leavesczy.compose_chat.widgets.CommonOutlinedTextField

@Composable
internal fun FriendshipDialog(viewState: FriendshipDialogViewState) {
    AnimatedBottomSheetDialog(
        modifier = Modifier,
        visible = viewState.isVisible,
        onDismissRequest = viewState.onDismissDialog
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.87f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 10.dp)
        ) {
            var userId by remember(key1 = viewState.isVisible) {
                mutableStateOf(value = "")
            }
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                value = userId,
                onValueChange = { id ->
                    val realValue = id.trimStart().trimEnd()
                    if (realValue.length <= 12 && realValue.all { char -> char.isLetterOrDigit() }) {
                        userId = realValue
                    }
                },
                label = stringResource(id = R.string.input_user_id),
                singleLine = true,
                maxLines = 1
            )
            CommonButton(text = stringResource(id = R.string.add_friend)) {
                if (userId.isBlank()) {
                    ToastProvider.showToast(resId = R.string.input_user_id_required)
                } else {
                    viewState.onAddFriend(userId)
                }
            }
            for (groupId in viewState.groupIds) {
                CommonButton(text = groupId.name) {
                    viewState.onJoinGroup(groupId.id)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 40.dp)
            )
        }
    }
}