package github.leavesczy.compose_chat.ui.chat.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.extensions.clickableNoRipple
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.chat.main.logic.InputSelector

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Composable
fun InputSelector(
    modifier: Modifier,
    inputSelector: InputSelector,
    onInputSelectorChanged: (inputSelector: InputSelector) -> Unit,
    sendMessageEnabled: Boolean,
    onClickSend: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputSelector(
            modifier = Modifier,
            icon = Icons.Outlined.Mood,
            selected = inputSelector == InputSelector.Emoji,
            onClick = {
                onInputSelectorChanged(InputSelector.Emoji)
            }
        )
        InputSelector(
            modifier = Modifier
                .padding(start = 16.dp),
            icon = Icons.Outlined.Topic,
            selected = inputSelector == InputSelector.Picture,
            onClick = {
                onInputSelectorChanged(InputSelector.Picture)
            }
        )
        Spacer(
            modifier = Modifier
                .weight(weight = 1f)
        )
        SendButton(
            modifier = Modifier,
            isEnabled = sendMessageEnabled,
            onClick = onClickSend
        )
    }
}

@Composable
private fun InputSelector(
    modifier: Modifier,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (selected) {
            AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color
        } else {
            AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(alpha = 0.46f)
        }
    )
    Icon(
        modifier = modifier
            .size(size = 26.dp)
            .clickableNoRipple(onClick = onClick),
        imageVector = icon,
        tint = color,
        contentDescription = null
    )
}

@Composable
private fun SendButton(
    modifier: Modifier,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 20.dp))
            .then(
                other = if (isEnabled) {
                    Modifier
                        .background(color = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color)
                        .clickable(onClick = onClick)
                } else {
                    Modifier
                        .background(
                            color = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(
                                alpha = 0.46f
                            )
                        )
                }
            )
            .padding(horizontal = 18.dp, vertical = 8.dp),
        text = stringResource(id = R.string.send),
        fontSize = 15.sp,
        lineHeight = 16.sp,
        color = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
    )
}