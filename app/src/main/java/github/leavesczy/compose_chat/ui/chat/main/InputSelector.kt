package github.leavesczy.compose_chat.ui.chat.main

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
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
enum class InputSelector {
    NONE,
    EMOJI,
    Picture,
}

@Composable
fun InputSelector(
    currentInputSelector: InputSelector,
    onInputSelectorChange: (InputSelector) -> Unit,
    sendMessageEnabled: Boolean,
    onClickSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputSelectorButton(
            icon = Icons.Outlined.Mood,
            selected = currentInputSelector == InputSelector.EMOJI,
            onClick = {
                onInputSelectorChange(InputSelector.EMOJI)
            }
        )
        InputSelectorButton(
            modifier = Modifier
                .padding(start = 16.dp),
            icon = Icons.Outlined.Topic,
            selected = currentInputSelector == InputSelector.Picture,
            onClick = {
                onInputSelectorChange(InputSelector.Picture)
            }
        )
        Spacer(
            modifier = Modifier
                .weight(weight = 1f)
        )
        Text(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = 20.dp))
                .then(
                    other = if (sendMessageEnabled) {
                        Modifier
                            .background(color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color)
                            .clickable(onClick = onClickSend)
                    } else {
                        Modifier
                            .background(
                                color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(
                                    alpha = 0.46f
                                )
                            )
                    }
                )
                .padding(horizontal = 18.dp, vertical = 6.dp),
            text = "Send",
            fontSize = 15.sp,
            lineHeight = 16.sp,
            color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
        )
    }
}

@Composable
private fun InputSelectorButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Icon(
        modifier = modifier
            .size(size = 26.dp)
            .clickableNoRipple(onClick = onClick),
        imageVector = icon,
        tint = if (selected) {
            ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color
        } else {
            ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(alpha = 0.46f)
        },
        contentDescription = null
    )
}