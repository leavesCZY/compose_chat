package github.leavesc.compose_chat.ui.weigets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.imePadding

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
        thickness = 1.dp,
        color = MaterialTheme.colors.secondary,
    )
}

@SuppressLint("ModifierParameter")
@Composable
fun CommonButton(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp), text: String, onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            onClick()
        }) {
        Text(
            text = text
        )
    }
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
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
        )
    )
}

@Composable
fun CommonSnackbar(state: SnackbarHostState) {
    SnackbarHost(state) { data ->
        Snackbar(
            modifier = Modifier.imePadding(),
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 0.dp,
            content = {
                Text(text = data.message, color = Color.White)
            },
        )
    }
}

@Composable
fun EmptyView() {
    Text(
        text = "Empty",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        fontSize = 49.sp,
    )
}