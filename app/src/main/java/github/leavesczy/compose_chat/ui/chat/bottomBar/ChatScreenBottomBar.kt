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
import github.leavesczy.compose_chat.common.SelectPictureContract

/**
 * @Author: leavesCZY
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private const val TEXT_MSG_MAX_LENGTH = 200

@Composable
fun ChatScreenBottomBar(
    messageInputted: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    sendText: (TextFieldValue) -> Unit,
    sendImage: (Uri) -> Unit
) {
    val textInputService = LocalTextInputService.current

    var currentInputSelector by remember {
        mutableStateOf(InputSelector.NONE)
    }
    var sendMessageEnabled by remember {
        mutableStateOf(false)
    }

    fun onMessageSent() {
        val text = messageInputted.text
        if (text.isNotBlank()) {
            sendText(messageInputted)
            onInputChange(TextFieldValue())
        }
    }

    fun onUserInputChanged(input: TextFieldValue) {
        val newMessage = if (messageInputted.text.length >= TEXT_MSG_MAX_LENGTH) {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                return
            }
            input
        } else {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                messageInputted.copy(text = input.text.substring(0, TEXT_MSG_MAX_LENGTH))
            } else {
                input
            }
        }
        onInputChange(newMessage)
    }

    fun onEmojiAppend(emoji: String) {
        if (messageInputted.text.length + emoji.length > TEXT_MSG_MAX_LENGTH) {
            return
        }
        val currentText = messageInputted.text
        val currentSelection = messageInputted.selection.end
        val currentSelectedText = currentText.substring(0, currentSelection)
        val messageAppend = currentSelectedText + emoji
        val selectedAppend = messageAppend.length
        onInputChange(
            TextFieldValue(
                text = messageAppend + currentText.substring(
                    currentSelection,
                    currentText.length
                ),
                selection = TextRange(index = selectedAppend)
            )
        )
    }

    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = SelectPictureContract()
    ) { imageUri ->
        if (imageUri != null) {
            sendImage(imageUri)
        }
    }

    val onSelectorChange: (InputSelector) -> Unit = remember {
        {
            if (it != currentInputSelector) {
                currentInputSelector = it
                if (currentInputSelector != InputSelector.NONE) {
                    textInputService?.hideSoftwareKeyboard()
                }
            }
        }
    }

    val ime = WindowInsets.ime
    val localDensity = LocalDensity.current
    val density = localDensity.density
    var imeMaxHeightDp by remember {
        mutableStateOf(0.dp)
    }

    LaunchedEffect(key1 = density) {
        snapshotFlow {
            ime.getBottom(localDensity)
        }.collect {
            val currentImeHeight = (it / density).dp
            if (currentImeHeight == imeMaxHeightDp) {
                currentInputSelector = InputSelector.NONE
            } else {
                imeMaxHeightDp = maxOf(imeMaxHeightDp, currentImeHeight)
            }
        }
    }

    SideEffect {
        sendMessageEnabled = messageInputted.text.isNotBlank()
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer),
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
            value = messageInputted,
            onValueChange = {
                onUserInputChanged(it)
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                onMessageSent()
            }),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            maxLines = 6
        )

        InputSelector(
            currentInputSelector = currentInputSelector,
            sendMessageEnabled = sendMessageEnabled,
            onInputSelectorChange = onSelectorChange,
            onMessageSent = {
                onMessageSent()
            }
        )

        Box(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max)
        ) {
            when (currentInputSelector) {
                InputSelector.NONE -> {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .imePadding()
                    )
                }
                InputSelector.EMOJI -> {
                    Box(modifier = Modifier.heightIn(min = imeMaxHeightDp)) {
                        EmojiTable(
                            onTextAdded = {
                                onEmojiAppend(it)
                            }
                        )
                    }
                }
                InputSelector.Picture -> {
                    Box(modifier = Modifier.heightIn(min = imeMaxHeightDp)) {
                        ExtendTable(selectPictureLauncher = selectPictureLauncher)
                    }
                }
            }
        }
    }

}