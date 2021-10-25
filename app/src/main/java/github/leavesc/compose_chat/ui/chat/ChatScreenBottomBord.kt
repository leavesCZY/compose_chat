package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.derivedWindowInsetsTypeOf
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * @Author: leavesC
 * @Date: 2021/7/5 23:33
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Preview(showBackground = true)
@Composable
fun ChatScreenBottomBordPreview() {
    ChatScreenBottomBord(sendMessage = {})
}

enum class InputSelector {
    NONE,
    EMOJI,
    Picture,
}

@Composable
fun ChatScreenBottomBord(
    sendMessage: (String) -> Unit
) {
    val textInputService = LocalTextInputService.current
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    var message by remember { mutableStateOf(TextFieldValue()) }
    var sendMessageEnabled by remember {
        mutableStateOf(false)
    }

    val onSelectorChange: (InputSelector) -> Unit = remember {
        {
            currentInputSelector = if (it == currentInputSelector) {
                InputSelector.NONE
            } else {
                it
            }
            if (currentInputSelector == InputSelector.NONE) {
                textInputService?.showSoftwareKeyboard()
            } else {
                textInputService?.hideSoftwareKeyboard()
            }
        }
    }

    if (currentInputSelector != InputSelector.NONE) {
        BackPressHandler(onBackPressed = {
            onSelectorChange(InputSelector.NONE)
        })
    }

    fun checkSendMessageEnabled() {
        sendMessageEnabled = message.text.isNotEmpty()
    }

    fun onMessageSent() {
        val text = message.text
        if (text.isNotEmpty()) {
            sendMessage(text)
            message = TextFieldValue()
        }
        checkSendMessageEnabled()
    }

    val ime = LocalWindowInsets.current.ime
    val navBars = LocalWindowInsets.current.navigationBars
    val insets = remember(key1 = ime, key2 = navBars) { derivedWindowInsetsTypeOf(ime, navBars) }
    val navigationBarsWithImePadding = rememberInsetsPaddingValues(
        insets = insets,
        applyStart = true,
        applyEnd = true,
        applyBottom = true
    )
    var navigationBarsWithImeHeight by remember {
        mutableStateOf(0.dp)
    }
    var bottomTableMinHeight by remember {
        mutableStateOf(0.dp)
    }

    LaunchedEffect(key1 = ime, key2 = navBars) {
        snapshotFlow {
            navigationBarsWithImePadding.calculateBottomPadding()
        }.distinctUntilChanged().filter {
            bottomTableMinHeight = if (currentInputSelector == InputSelector.NONE) {
                if (ime.isVisible) {
                    navigationBarsWithImeHeight
                } else {
                    0.dp
                }
            } else {
                navigationBarsWithImeHeight
            }
            it > navigationBarsWithImeHeight
        }.collect {
            navigationBarsWithImeHeight = it
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.secondary),
    ) {
        BasicTextField(
            modifier = Modifier
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
                val msg = it.text
                message = if (msg.length < 200) {
                    it
                } else {
                    TextFieldValue(text = msg.substring(0, 200))
                }
                checkSendMessageEnabled()
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                onMessageSent()
            }),
            textStyle = MaterialTheme.typography.subtitle1.copy(letterSpacing = 2.sp),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            maxLines = 6,
        )

        UserInputSelector(
            currentInputSelector = currentInputSelector,
            sendMessageEnabled = sendMessageEnabled,
            onInputSelectorChange = onSelectorChange,
            onMessageSent = {
                onMessageSent()
            },
        )

        Box(modifier = Modifier.sizeIn(minHeight = bottomTableMinHeight)) {
            val input: Unit = when (currentInputSelector) {
                InputSelector.NONE -> {

                }
                InputSelector.EMOJI -> {
                    EmojiTable(
                        onTextAdded = {
                            val currentText = message.text
                            val currentSelection = message.selection.end
                            val currentSelectedText = currentText.substring(0, currentSelection)
                            val messageAppend = currentSelectedText + it
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
                    )
                }
                InputSelector.Picture -> {

                }
            }
        }
    }
}