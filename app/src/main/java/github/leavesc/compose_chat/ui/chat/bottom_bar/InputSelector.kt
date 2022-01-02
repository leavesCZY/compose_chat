package github.leavesc.compose_chat.ui.chat.bottom_bar

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @Author: leavesC
 * @Date: 2022/1/2 15:02
 * @Desc:
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewEmojiTable() {
    InputSelector(currentInputSelector = InputSelector.EMOJI,
        onInputSelectorChange = {

        }, sendMessageEnabled = true, onMessageSent = {

        })
}

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
        val contentColor = MaterialTheme.colors.primary
        val disabledContentColor = contentColor.copy(alpha = ContentAlpha.disabled)
        val buttonColors = ButtonDefaults.buttonColors(
            backgroundColor = contentColor,
            contentColor = contentColor,
            disabledBackgroundColor = disabledContentColor,
            disabledContentColor = disabledContentColor
        )
        Button(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
            colors = buttonColors,
            shape = MaterialTheme.shapes.large,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Send"
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
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
            },
            contentDescription = null
        )
    }
}