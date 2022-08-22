package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.model.PersonProfilePageViewState
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.BouncyImage
import github.leavesczy.compose_chat.ui.widgets.CoilImage

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun PersonProfilePage(viewState: PersonProfilePageViewState) {
    val personProfile = viewState.personProfile
    val faceUrl = personProfile.faceUrl
    val title = personProfile.showName
    val subtitle = personProfile.signature
    val introduction = "ID: ${personProfile.id}"
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        ConstraintLayout(modifier = Modifier) {
            val (backgroundRef, avatarRef, titleRef, subtitleRef, introductionRef) = createRefs()
            CoilImage(modifier = Modifier
                .constrainAs(ref = backgroundRef) {
                    linkTo(start = parent.start, end = parent.end)
                    top.linkTo(anchor = parent.top)
                    width = Dimension.fillToConstraints
                }
                .aspectRatio(ratio = 1f)
                .scrim(color = Color(0x51444B53)),
                data = faceUrl
            )
            BouncyImage(
                modifier = Modifier
                    .statusBarsPadding()
                    .constrainAs(ref = avatarRef) {
                        linkTo(
                            top = backgroundRef.top,
                            bottom = backgroundRef.bottom,
                            bias = 0.1f
                        )
                        linkTo(start = backgroundRef.start, end = backgroundRef.end)
                    }
                    .size(size = 90.dp)
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        if (faceUrl.isNotBlank()) {
                            PreviewImageActivity.navTo(context = context, imagePath = faceUrl)
                        }
                    },
                data = faceUrl
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = titleRef) {
                        linkTo(start = backgroundRef.start, end = backgroundRef.end)
                        top.linkTo(anchor = avatarRef.bottom, margin = 10.dp)
                    }
                    .padding(horizontal = 10.dp),
                text = title,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = subtitleRef) {
                        linkTo(start = backgroundRef.start, end = backgroundRef.end)
                        top.linkTo(anchor = titleRef.bottom, margin = 10.dp)
                    }
                    .padding(horizontal = 10.dp),
                text = subtitle,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = introductionRef) {
                        linkTo(start = backgroundRef.start, end = backgroundRef.end)
                        top.linkTo(anchor = subtitleRef.bottom, margin = 10.dp)
                    }
                    .padding(horizontal = 10.dp),
                text = introduction,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}