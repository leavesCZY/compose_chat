package github.leavesc.compose_chat.ui.profile

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
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
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.extend.navToPreviewImageScreen
import github.leavesc.compose_chat.ui.weigets.BezierImage
import github.leavesc.compose_chat.ui.weigets.CircleBorderImage
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
    val navHostController = LocalNavHostController.current
    ConstraintLayout {
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
        BezierImage(modifier = Modifier.constrainAs(ref = backgroundRefs) {

        }, data = avatarUrl)
        CircleBorderImage(
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
                .clickable {
                    if (avatarUrl.isNotBlank()) {
                        navHostController.navToPreviewImageScreen(imagePath = avatarUrl)
                    }
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
            style = MaterialTheme.typography.titleLarge,
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
            style = MaterialTheme.typography.titleMedium,
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
            style = MaterialTheme.typography.bodyMedium,
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