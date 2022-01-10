package github.leavesc.compose_chat.ui.weigets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Author: leavesC
 * @Date: 2021/7/3 18:57
 * @Desc:
 * @Github：https://github.com/leavesC
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
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .then(other = modifier),
        content = {
            Text(text = text)
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
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.disabled),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun EmptyView() {
    Text(
        text = "Empty",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        fontSize = 49.sp,
    )
}