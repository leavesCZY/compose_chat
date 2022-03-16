package github.leavesczy.compose_chat.ui.chat.bottomBar

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import github.leavesczy.compose_chat.common.SelectPictureContract
import github.leavesczy.compose_chat.utils.log
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private const val TEXT_MSG_MAX_LENGTH = 200

@Composable
fun ChatScreenBottomBar(
    sendText: (String) -> Unit,
    sendImage: (Uri) -> Unit
) {
    val textInputService = LocalTextInputService.current
    var currentInputSelector by remember { mutableStateOf(InputSelector.NONE) }
    var message by remember { mutableStateOf(TextFieldValue()) }
    var sendMessageEnabled by remember {
        mutableStateOf(false)
    }

    val onSelectorChange: (InputSelector) -> Unit = remember {
        {
            if (it != currentInputSelector) {
                currentInputSelector = it
                if (currentInputSelector != InputSelector.NONE) {
                    textInputService?.hideSoftwareKeyboard()
                } else {
                    textInputService?.showSoftwareKeyboard()
                }
            }
        }
    }

    fun checkSendMessageEnabled() {
        sendMessageEnabled = message.text.isNotBlank()
    }

    fun onMessageSent() {
        val text = message.text
        if (text.isNotBlank()) {
            sendText(text)
            message = TextFieldValue()
        }
        checkSendMessageEnabled()
    }

    fun onInputChanged(input: TextFieldValue) {
        message = if (message.text.length >= TEXT_MSG_MAX_LENGTH) {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                return
            }
            input
        } else {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                message.copy(text = input.text.substring(0, TEXT_MSG_MAX_LENGTH))
            } else {
                input
            }
        }
        checkSendMessageEnabled()
    }

    fun onEmojiAppend(emoji: String) {
        if (message.text.length + emoji.length > TEXT_MSG_MAX_LENGTH) {
            return
        }
        val currentText = message.text
        val currentSelection = message.selection.end
        val currentSelectedText = currentText.substring(0, currentSelection)
        val messageAppend = currentSelectedText + emoji
        val selectedAppend = messageAppend.length
        message = TextFieldValue(
            text = messageAppend + currentText.substring(
                currentSelection,
                currentText.length
            ),
            selection = TextRange(index = selectedAppend)
        )
        checkSendMessageEnabled()
    }

    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = SelectPictureContract()
    ) { imageUri ->
        if (imageUri != null) {
            sendImage(imageUri)
        }
    }
    val ime = WindowInsets.ime
    val mLocalDensity = LocalDensity.current

    LaunchedEffect(key1 = "") {
        snapshotFlow {
            log {
                WindowInsetsCompat.CONSUMED.isVisible(WindowInsetsCompat.Type.ime())
            }
            ime.getBottom(mLocalDensity)
        }.filter {
            it > 0
        }.collect {
            onSelectorChange(InputSelector.NONE)
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
            .imePadding(),
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp, end = 12.dp, top = 12.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(size = 6.dp)
                )
                .padding(all = 8.dp),
            value = message,
            onValueChange = {
                onInputChanged(it)
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                onMessageSent()
            }),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            maxLines = 6,
        )

        InputSelector(
            currentInputSelector = currentInputSelector,
            sendMessageEnabled = sendMessageEnabled,
            onInputSelectorChange = onSelectorChange,
            onMessageSent = {
                onMessageSent()
            },
        )

        Box {
            when (currentInputSelector) {
                InputSelector.NONE -> {

                }
                InputSelector.EMOJI -> {
                    EmojiTable(
                        onTextAdded = {
                            onEmojiAppend(it)
                        }
                    )
                }
                InputSelector.Picture -> {
                    ExtendTable(selectPictureLauncher = selectPictureLauncher)
                }
            }
        }
    }
}