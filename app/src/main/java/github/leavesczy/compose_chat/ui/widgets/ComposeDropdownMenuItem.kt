package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Date: 2026/1/5 12:14
 * @Desc:
 */
@Composable
internal fun ComposeDropdownMenuItem(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        modifier = modifier,
        text = {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                text = text,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
            )
        },
        onClick = onClick
    )
}