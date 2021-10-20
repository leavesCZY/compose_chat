package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding

/**
 * @Author: leavesC
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun ChatScreenBottomBord(
    sendMessage: (String) -> Unit
) {
//    val ime = LocalWindowInsets.current.ime
//    val navBars = LocalWindowInsets.current.navigationBars
//    val insets = remember(ime, navBars) { derivedWindowInsetsTypeOf(ime, navBars) }
//    val paddingValues = rememberInsetsPaddingValues(
//        insets = insets,
//        applyStart = true,
//        applyEnd = true,
//        applyBottom = true
//    )
//    Log.e("TAG", "insets.layoutInsets.bottom: " + insets.layoutInsets.bottom)
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .navigationBarsWithImePadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var message by remember { mutableStateOf("") }
        var enabledSend by remember {
            mutableStateOf(false)
        }

        fun send() {
            if (message.isBlank()) {
                return
            }
            sendMessage(message)
            message = ""
            enabledSend = message.trim().isNotEmpty()
        }
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(
                    start = 8.dp, bottom = 8.dp, top = 8.dp
                ),
            value = message,
            onValueChange = {
                message = if (it.length > 200) {
                    it.substring(0, 200)
                } else {
                    it
                }
                enabledSend = message.trim().isNotEmpty()
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                send()
            }),
            textStyle = MaterialTheme.typography.subtitle1,
            maxLines = 4,
        )
        Button(modifier = Modifier.padding(all = 8.dp), enabled = enabledSend,
            onClick = {
                send()
            }) {
            Text(text = "发送", style = MaterialTheme.typography.subtitle1)
        }
    }
}