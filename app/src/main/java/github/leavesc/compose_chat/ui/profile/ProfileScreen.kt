package github.leavesc.compose_chat.ui.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.extend.scrim
import github.leavesc.compose_chat.ui.theme.BezierShape
import github.leavesc.compose_chat.ui.weigets.CoilImage
import github.leavesc.compose_chat.ui.weigets.OutlinedAvatar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Preview
@Composable
private fun PreviewProfileScreen() {
    ProfileScreen(personProfile = PersonProfile.Empty)
}

@Composable
fun ProfileScreen(personProfile: PersonProfile) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .zIndex(zIndex = 1f)
    ) {
        val userFaceUrl = personProfile.faceUrl
        val userId = personProfile.userId
        val userName = personProfile.showName
        val userSignature = personProfile.signature
        val animateValue by rememberInfiniteTransition().animateFloat(
            initialValue = 1.3f, targetValue = 1.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
        val (backgroundRefs, idRefs, faceRefs, nicknameRefs, signatureRefs) = createRefs()
        CoilImage(
            data = userFaceUrl,
            modifier = Modifier
                .constrainAs(ref = backgroundRefs) {

                }
                .fillMaxWidth()
                .aspectRatio(ratio = 5f / 4f)
                .clip(shape = BezierShape(padding = animateValue * 100))
                .scrim(colors = listOf(Color(0x40000000), Color(0x40F4F4F4)))
                .scale(scale = animateValue)
                .rotate(degrees = animateValue * 10.3f)
        )
        val avatarSize = 90.dp
        val coroutineScope = rememberCoroutineScope()
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        OutlinedAvatar(
            data = userFaceUrl,
            modifier = Modifier
                .constrainAs(ref = faceRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    bottom.linkTo(backgroundRefs.bottom)
                }
                .size(size = avatarSize)
                .zIndex(zIndex = 1f)
                .offset {
                    IntOffset(
                        x = offsetX.roundToInt(),
                        y = offsetY.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {

                        },
                        onDragCancel = {

                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                Animatable(
                                    initialValue = Offset(offsetX, offsetY),
                                    typeConverter = Offset.VectorConverter
                                ).animateTo(
                                    targetValue = Offset(x = 0f, y = 0f),
                                    animationSpec = SpringSpec(dampingRatio = Spring.DampingRatioHighBouncy),
                                    block = {
                                        offsetX = value.x
                                        offsetY = value.y
                                    }
                                )
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        },
                    )
                }
        )
        Text(text = userName,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = nicknameRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    top.linkTo(parent.top)
                }
                .statusBarsPadding()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp))
        Text(
            text = userSignature,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = signatureRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    top.linkTo(nicknameRefs.bottom)
                }
                .padding(
                    start = 15.dp, end = 15.dp, top = 8.dp
                ))
        Text(
            text = "ID: $userId",
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = idRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    top.linkTo(backgroundRefs.bottom)
                }
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
        )
    }
}