package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * @Author: leavesCZY
 * @Date: 2022/1/2 15:02
 * @Desc:
 */
enum class InputSelector {
    NONE,
    EMOJI,
    Picture,
}

@Composable
fun InputSelector(
    modifier: Modifier = Modifier,
    currentInputSelector: InputSelector,
    onInputSelectorChange: (InputSelector) -> Unit,
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit,
) {
    Row(
        modifier = modifier
            .wrapContentHeight(),
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
            icon = Icons.Outlined.Topic,
            selected = currentInputSelector == InputSelector.Picture,
            onClick = {
                onInputSelectorChange(InputSelector.Picture)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.46f)
        )
        Button(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
            colors = buttonColors
        ) {
            Text(
                modifier = Modifier,
                text = "Send",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun InputSelectorButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = Modifier
                .padding(all = 8.dp)
                .size(size = 26.dp),
            imageVector = icon,
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.46f)
            },
            contentDescription = null
        )
    }
}