package github.leavesczy.compose_chat.ui.person

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.person.logic.PersonProfilePageViewState
import github.leavesczy.compose_chat.ui.widgets.AnimateBouncyImage
import github.leavesczy.compose_chat.ui.widgets.BezierImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun PersonProfilePage(pageViewState: PersonProfilePageViewState) {
    val personProfile = pageViewState.personProfile
    val faceUrl = personProfile.faceUrl
    val title = personProfile.showName
    val subtitle = personProfile.signature
    val introduction = "ID: ${personProfile.id}"
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
                    model = faceUrl,
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