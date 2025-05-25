package github.leavesczy.compose_chat.ui.chat

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.widgets.MatisseImageEngine
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseCapture
import github.leavesczy.matisse.MatisseCaptureContract
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaStoreCaptureStrategy
import github.leavesczy.matisse.MediaType

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private val DEFAULT_KEYBOARD_HEIGHT = 305.dp

@Composable
fun ChatPageBottomBar(chatViewModel: ChatViewModel) {
    val textMessageInputted = chatViewModel.textMessageInputted
    val currentInputSelector = chatViewModel.currentInputSelector
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current
    BackHandler(enabled = currentInputSelector != InputSelector.NONE) {
        focusRequester.requestFocus()
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = MatisseContract()
    ) { result ->
        if (!result.isNullOrEmpty()) {
            chatViewModel.sendImageMessage(imageUri = result[0].uri)
        }
    }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = MatisseCaptureContract()
    ) { result ->
        if (result != null) {
            chatViewModel.sendImageMessage(imageUri = result.uri)
        }
    }
    var keyboardHeightDp by remember {
        mutableStateOf(value = 0.dp)
    }
    val ime = WindowInsets.ime
    val localDensity = LocalDensity.current
    val density = localDensity.density
    LaunchedEffect(key1 = density) {
        snapshotFlow {
            ime.getBottom(density = localDensity)
        }.collect {
            val realtimeKeyboardHeightDp = (it / density).dp
            keyboardHeightDp = maxOf(
                realtimeKeyboardHeightDp, keyboardHeightDp
            )
            if (realtimeKeyboardHeightDp == keyboardHeightDp) {
                chatViewModel.onInputSelectorChanged(newSelector = InputSelector.NONE)
                softwareKeyboardController?.show()
            }
        }
    }
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
            .navigationBarsPadding(),
    ) {
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester = focusRequester)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 12.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(size = 10.dp)
                )
                .padding(horizontal = 8.dp, vertical = 12.dp),
            value = textMessageInputted,
            onValueChange = {
                chatViewModel.onUserInputChanged(input = it)
            },
            maxLines = 6,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(value = MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    focusRequester.requestFocus()
                    chatViewModel.sendTextMessage()
                }
            )
        )
        InputSelector(
            currentInputSelector = currentInputSelector,
            sendMessageEnabled = textMessageInputted.text.isNotEmpty(),
            onInputSelectorChange = {
                focusManager.clearFocus(force = true)
                softwareKeyboardController?.hide()
                chatViewModel.onInputSelectorChanged(newSelector = it)
            },
            onClickSend = {
                focusRequester.requestFocus()
                chatViewModel.sendTextMessage()
            }
        )
        when (currentInputSelector) {
            InputSelector.NONE -> {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .imePadding()
                )
            }

            InputSelector.EMOJI, InputSelector.Picture -> {
                val maxHeight = if (keyboardHeightDp <= 0.dp) {
                    DEFAULT_KEYBOARD_HEIGHT
                } else {
                    keyboardHeightDp
                }
                Box(
                    modifier = Modifier
                        .heightIn(min = keyboardHeightDp, max = maxHeight)
                ) {
                    when (currentInputSelector) {
                        InputSelector.EMOJI -> {
                            EmojiTable(
                                appendEmoji = {
                                    chatViewModel.appendEmoji(emoji = it)
                                }
                            )
                        }

                        InputSelector.Picture -> {
                            Box(
                                modifier = Modifier
                                    .heightIn(
                                        min = keyboardHeightDp,
                                        max = maxHeight
                                    )
                            ) {
                                ExtendTable(
                                    launchImagePicker = {
                                        chatViewModel.onInputSelectorChanged(newSelector = InputSelector.NONE)
                                        val matisse = Matisse(
                                            maxSelectable = 1,
                                            gridColumns = 4,
                                            fastSelect = false,
                                            mediaType = MediaType.ImageOnly,
                                            imageEngine = MatisseImageEngine(),
                                            captureStrategy = MediaStoreCaptureStrategy()
                                        )
                                        imagePickerLauncher.launch(matisse)
                                    },
                                    launchTakePicture = {
                                        chatViewModel.onInputSelectorChanged(newSelector = InputSelector.NONE)
                                        val matisseCapture =
                                            MatisseCapture(captureStrategy = MediaStoreCaptureStrategy())
                                        takePictureLauncher.launch(matisseCapture)
                                    }
                                )
                            }
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }
}