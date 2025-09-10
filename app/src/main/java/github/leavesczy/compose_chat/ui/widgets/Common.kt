package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun CommonOutlinedTextField(
    modifier: Modifier,
    value: String,
    label: String,
    singleLine: Boolean = false,
    maxLines: Int = 4,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLines,
        label = {
            Text(
                modifier = Modifier,
                text = label,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
            )
        },
        textStyle = TextStyle(
            fontSize = 17.sp,
            color = ComposeChatTheme.colorScheme.c_FF1C1B1F_FFFFFFFF.color
        ),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
            focusedBorderColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(alpha = 0.7f),
            unfocusedBorderColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun CommonButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color),
        content = {
            Text(
                text = text,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            )
        },
        onClick = onClick
    )
}