package github.leavesc.compose_chat.ui.profile

import android.content.res.Configuration
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
import github.leavesc.compose_chat.base.model.GroupProfile
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(
        personProfile = PersonProfile(
            userId = "leavesC",
            faceUrl = "",
            nickname = "业志陈",
            remark = "",
            signature = "希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23\uD83E\uDD23",
            isFriend = false
        ),
        content = {

        }
    )
}

@Composable
fun ProfileScreen(
    personProfile: PersonProfile,
    content: @Composable () -> Unit = {}
) {
    ProfileScreen(
        title = personProfile.showName,
        subtitle = personProfile.signature,
        introduction = "ID: ${personProfile.userId}",
        avatarUrl = personProfile.faceUrl,
        content = content
    )
}

@Composable
fun ProfileScreen(
    groupProfile: GroupProfile,
    content: @Composable () -> Unit = {}
) {
    ProfileScreen(
        title = groupProfile.name,
        subtitle = groupProfile.introduction,
        introduction = "GroupID: ${groupProfile.id}\nCreateTime: ${groupProfile.createTimeFormat}\nMemberCount: ${groupProfile.memberCount}",
        avatarUrl = groupProfile.faceUrl,
        content = content
    )
}

@Composable
private fun ProfileScreen(
    title: String,
    subtitle: String,
    introduction: String,
    avatarUrl: String,
    content: @Composable () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
    ) {
        val animateValue by rememberInfiniteTransition().animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        val coroutineScope = rememberCoroutineScope()
        fun launchDragAnimate() {
            coroutineScope.launch {
                Animatable(
                    initialValue = Offset(x = offsetX, y = offsetY),
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
        }

        val (titleRefs, subtitleRefs, introductionRefs, backgroundRefs, avatarRefs, contentRefs) = createRefs()
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = backgroundRefs) {

                }
                .fillMaxWidth()
                .aspectRatio(ratio = 5f / 4f)
                .zIndex(zIndex = -100f)
                .scale(scale = (animateValue + 1f) * 1.1f)
                .clip(shape = BezierShape(animateValue = animateValue))
                .rotate(degrees = animateValue * 10f)
                .scrim(colors = listOf(Color(color = 0x4DD1D5D8), Color(color = 0x41F7F5F5))),
            data = avatarUrl
        )
        OutlinedAvatar(
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    bottom.linkTo(anchor = backgroundRefs.bottom)
                }
                .size(size = 100.dp)
                .zIndex(zIndex = Float.MAX_VALUE)
                .offset {
                    IntOffset(
                        x = offsetX.roundToInt(),
                        y = offsetY.roundToInt()
                    )
                }
                .pointerInput(key1 = Unit) {
                    detectDragGestures(
                        onDragStart = {

                        },
                        onDragCancel = {
                            launchDragAnimate()
                        },
                        onDragEnd = {
                            launchDragAnimate()
                        },
                        onDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        },
                    )
                },
            data = avatarUrl
        )
        Text(text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = titleRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    top.linkTo(anchor = parent.top)
                }
                .statusBarsPadding()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = subtitleRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    top.linkTo(anchor = titleRefs.bottom)
                }
                .padding(
                    start = 15.dp, end = 15.dp, top = 12.dp
                ))
        Text(
            text = introduction,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = introductionRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    top.linkTo(anchor = backgroundRefs.bottom)
                }
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
        )
        Box(
            modifier = Modifier
                .constrainAs(ref = contentRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    top.linkTo(anchor = introductionRefs.bottom)
                }
                .zIndex(zIndex = 1f)
        ) {
            content()
        }
    }
}