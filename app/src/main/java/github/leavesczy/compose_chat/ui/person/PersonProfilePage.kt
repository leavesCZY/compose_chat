package github.leavesczy.compose_chat.ui.person

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.person.logic.PersonProfilePageViewState
import github.leavesczy.compose_chat.ui.widgets.BezierImage
import github.leavesczy.compose_chat.ui.widgets.BouncyImage
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun PersonProfilePage(pageViewState: PersonProfilePageViewState) {
    val personProfile by pageViewState.personProfile
    val faceUrl = personProfile.faceUrl
    val title = personProfile.showName
    val subtitle = personProfile.signature
    val introduction = "ID: ${personProfile.id}"
    var animated by remember {
        mutableStateOf(value = false)
    }
    LaunchedEffect(key1 = null) {
        animated = true
        delay(timeMillis = 1000)
        animated = false
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier) {
            BezierImage(
                modifier = Modifier
                    .aspectRatio(ratio = 1f)
                    .scrim(color = Color(0x4D000000)),
                model = faceUrl
            )
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimateBouncyImage(
                    modifier = Modifier
                        .size(size = 100.dp)
                        .clickableNoRipple {
                            pageViewState.previewImage(faceUrl)
                        },
                    faceUrl = faceUrl,
                    animated = animated
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    text = title,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    text = subtitle,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    text = introduction,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AnimateBouncyImage(
    modifier: Modifier,
    faceUrl: String,
    animated: Boolean
) {
    val scale by animateFloatAsState(
        label = "",
        targetValue = if (animated) {
            1.8f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 1000)
    )
    val offset by animateIntOffsetAsState(
        label = "",
        targetValue = if (animated) {
            IntOffset(
                x = 0,
                y = Random.nextInt(500, 1000)
            )
        } else {
            IntOffset(x = 0, y = 0)
        }
    )
    BouncyImage(
        modifier = modifier
            .offset {
                offset
            }
            .scale(scale = scale),
        data = faceUrl
    )
}