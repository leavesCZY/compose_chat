package github.leavesczy.compose_chat.ui.chat.main

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageBottomBarViewState
import github.leavesczy.compose_chat.ui.chat.main.logic.InputSelector
import github.leavesczy.compose_chat.widgets.MatisseImageEngine
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseCapture
import github.leavesczy.matisse.MatisseCaptureContract
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaStoreCaptureStrategy
import github.leavesczy.matisse.MediaType

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Composable
fun ChatPageBottomBar(
    modifier: Modifier,
    bottomBarViewState: ChatPageBottomBarViewState
) {
    val localDensity = LocalDensity.current
    val localFocusManager = LocalFocusManager.current
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    val ime = WindowInsets.ime
    val focusRequester = remember {
        FocusRequester()
    }
    BackHandler(enabled = bottomBarViewState.inputSelector != InputSelector.None) {
        bottomBarViewState.onInputSelectorChanged(InputSelector.None)
    }
    var keyboardHeightDp by remember {
        mutableStateOf(value = 0.dp)
    }
    LaunchedEffect(key1 = localDensity) {
        snapshotFlow {
            ime.getBottom(density = localDensity)
        }.collect { bottomInset ->
            val realtimeKeyboardHeightDp = (bottomInset / localDensity.density).dp
            keyboardHeightDp = maxOf(realtimeKeyboardHeightDp, keyboardHeightDp)
            if (realtimeKeyboardHeightDp == keyboardHeightDp) {
                bottomBarViewState.onInputSelectorChanged(InputSelector.None)
                localSoftwareKeyboardController?.show()
            }
        }
    }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = MatisseCaptureContract()
    ) { result ->
        if (result != null) {
            bottomBarViewState.onSendImageMessage(result.uri)
        }
    }
    val matisseLauncher = rememberLauncherForActivityResult(
        contract = MatisseContract()
    ) { result ->
        if (!result.isNullOrEmpty()) {
            bottomBarViewState.onSendImageMessage(result[0].uri)
        }
    }
    val pickVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            bottomBarViewState.onSendImageMessage(uri)
        }
    }
    Column(
        modifier = modifier
            .background(color = AppTheme.colorScheme.c_FFEFF1F3_FF22202A.color)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.Top
        )
    ) {
        val textFieldState = rememberTextFieldState(initialText = "")
        val sendMessageEnabled by remember {
            derivedStateOf {
                textFieldState.text.isNotBlank()
            }
        }

        fun onClickSend() {
            val text = textFieldState.text.toString()
            if (text.isNotBlank()) {
                focusRequester.requestFocus()
                bottomBarViewState.onSendTextMessage(text)
                textFieldState.clearText()
            }
        }
        TextField(
            modifier = Modifier
                .focusRequester(focusRequester = focusRequester),
            textFieldState = textFieldState,
            onClickSend = {
                onClickSend()
            }
        )
        InputSelector(
            modifier = Modifier,
            inputSelector = bottomBarViewState.inputSelector,
            sendMessageEnabled = sendMessageEnabled,
            onInputSelectorChanged = { inputSelector ->
                localFocusManager.clearFocus(force = true)
                localSoftwareKeyboardController?.hide()
                bottomBarViewState.onInputSelectorChanged(inputSelector)
            },
            onClickSend = {
                onClickSend()
            }
        )
        val maxHeight = if (keyboardHeightDp <= 0.dp) {
            270.dp
        } else {
            keyboardHeightDp
        }
        when (bottomBarViewState.inputSelector) {
            InputSelector.None -> {
                KeyboardSpace(modifier = Modifier)
            }

            InputSelector.Emoji -> {
                Box(
                    modifier = Modifier
                        .heightIn(
                            min = keyboardHeightDp,
                            max = maxHeight
                        )
                        .navigationBarsPadding()
                ) {
                    EmojiTable(
                        modifier = Modifier,
                        appendEmoji = { emoji ->
                            textFieldState.insertAtCursor(insert = emoji)
                        }
                    )
                }
            }

            InputSelector.Picture -> {
                Box(
                    modifier = Modifier
                        .heightIn(
                            min = keyboardHeightDp,
                            max = maxHeight
                        )
                ) {
                    MediaPickerTable(
                        modifier = Modifier,
                        onClickImagePicker = {
                            bottomBarViewState.onInputSelectorChanged(InputSelector.None)
                            if (bottomBarViewState.isPhotoPickerAvailable) {
                                pickVisualMediaLauncher.launch(
                                    input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                val matisse = Matisse(
                                    maxSelectable = 1,
                                    gridColumns = 4,
                                    fastSelect = false,
                                    mediaType = MediaType.ImageOnly,
                                    imageEngine = MatisseImageEngine(),
                                    captureStrategy = MediaStoreCaptureStrategy()
                                )
                                matisseLauncher.launch(input = matisse)
                            }
                        },
                        onClickTakePicture = {
                            bottomBarViewState.onInputSelectorChanged(InputSelector.None)
                            val matisseCapture =
                                MatisseCapture(captureStrategy = MediaStoreCaptureStrategy())
                            takePictureLauncher.launch(input = matisseCapture)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TextField(
    modifier: Modifier,
    textFieldState: TextFieldState,
    onClickSend: () -> Unit
) {
    BasicTextField(
        modifier = modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth(),
        state = textFieldState,
        lineLimits = TextFieldLineLimits.MultiLine(
            minHeightInLines = 1,
            maxHeightInLines = 6
        ),
        inputTransformation = InputTransformation
            .maxLength(maxLength = 1000),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        onKeyboardAction = {
            onClickSend()
        },
        cursorBrush = AppTheme.cursorColor,
        textStyle = TextStyle(
            fontSize = 18.sp,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None
            ),
            letterSpacing = 1.sp,
            color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
        ),
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(size = 10.dp))
                    .background(color = AppTheme.colorScheme.c_FFFFFFFF_FF101010.color)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                innerTextField()
            }
        }
    )
}

private fun TextFieldState.insertAtCursor(insert: String) {
    edit {
        val currentSelection = selection
        val start = currentSelection.start
        val end = currentSelection.end
        replace(start = start, end = end, text = insert)
        val nextCharIndex = start + insert.length
        if (nextCharIndex <= length) {
            placeCursorBeforeCharAt(nextCharIndex)
        } else {
            selection = TextRange(length)
        }
    }
}

@Composable
private fun KeyboardSpace(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .windowInsetsPadding(
                insets = WindowInsets.navigationBars
                    .union(insets = WindowInsets.ime)
            )
    )
}