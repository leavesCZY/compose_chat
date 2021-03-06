package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Author: leavesCZY
 * @Date: 2021/7/3 18:57
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun CommonDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        thickness = 0.8.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
    )
}

@Composable
fun CommonButton(
    modifier: Modifier = Modifier, text: String, onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .then(other = modifier),
        content = {
            Text(text = text, fontSize = 16.sp, color = Color.White)
        },
        onClick = {
            onClick()
        }
    )
}

@Composable
fun CommonOutlinedTextField(
    modifier: Modifier,
    value: String,
    label: String,
    singleLine: Boolean = false,
    maxLines: Int = 4,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLines,
        label = {
            Text(text = label)
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun EmptyView(contentPadding: PaddingValues = PaddingValues(all = 0.dp)) {
    Text(
        text = "Empty",
        modifier = Modifier
            .padding(paddingValues = contentPadding)
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp
    )
}