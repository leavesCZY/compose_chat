package github.leavesc.compose_chat.ui.chat.bottom_bar

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.LocalWindowInsets
import github.leavesc.compose_chat.common.SelectPictureContract
import github.leavesc.compose_chat.ui.weigets.navigationBarsWithImePadding
import github.leavesc.compose_chat.utils.log
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewChatScreenBottomBord() {
    ChatScreenBottomBar(sendText = {}, sendImage = {})
}

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

    val focusRequester = remember { FocusRequester() }

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

    BackHandler(enabled = currentInputSelector != InputSelector.NONE, onBack = {
        focusRequester.requestFocus()
    })

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

    val ime = LocalWindowInsets.current.ime
    val navBars = LocalWindowInsets.current.navigationBars
    val navigationBarsWithImePadding = navigationBarsWithImePadding()

    var navigationBarsWithImeHeight by remember {
        mutableStateOf(0.dp)
    }
    var bottomTableMinHeight by remember {
        mutableStateOf(0.dp)
    }
    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = SelectPictureContract()
    ) { imageUri ->
        log {
            "imageUri: " + imageUri?.toString()
        }
        if (imageUri != null) {
            sendImage(imageUri)
        }
    }
    LaunchedEffect(key1 = ime, key2 = navBars) {
        launch {
            snapshotFlow {
                ime.animationInProgress
            }.filter {
                !it && ime.isVisible && currentInputSelector != InputSelector.NONE
            }.collect {
                currentInputSelector = InputSelector.NONE
            }
        }
        launch {
            snapshotFlow {
                ime.isVisible
            }.filter {
                !it
            }.collect {
                navigationBarsWithImeHeight =
                    navigationBarsWithImePadding.calculateBottomPadding()
            }
        }
        launch {
            snapshotFlow {
                navigationBarsWithImePadding.calculateBottomPadding()
            }.collect {
                if (ime.isVisible) {
                    if (it != 0.dp) {
                        bottomTableMinHeight = if (currentInputSelector == InputSelector.NONE) {
                            it
                        } else {
                            if (navigationBarsWithImeHeight.value > 0f) {
                                navigationBarsWithImeHeight
                            } else {
                                it
                            }
                        }
                    }
                } else {
                    bottomTableMinHeight = when (currentInputSelector) {
                        InputSelector.NONE -> {
                            it
                        }
                        InputSelector.EMOJI, InputSelector.Picture -> {
                            navigationBarsWithImeHeight
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer),
    ) {
        BasicTextField(
            modifier = Modifier
                .focusTarget()
                .focusRequester(focusRequester = focusRequester)
                .fillMaxWidth()
                .padding(
                    start = 12.dp, end = 12.dp, top = 12.dp
                )
                .background(
                    color = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.small
                )
                .padding(all = 8.dp),
            value = message,
            onValueChange = {
                onInputChanged(it)
            },
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

        Box(
            modifier = Modifier.sizeIn(minHeight = bottomTableMinHeight)
        ) {
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