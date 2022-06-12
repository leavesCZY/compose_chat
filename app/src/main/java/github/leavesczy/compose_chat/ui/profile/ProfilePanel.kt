package github.leavesczy.compose_chat.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import github.leavesczy.compose_chat.common.model.GroupProfile
import github.leavesczy.compose_chat.common.model.PersonProfile
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToPreviewImagePage
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.widgets.BezierImage
import github.leavesczy.compose_chat.ui.widgets.BouncyImage

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ProfilePanel(
    personProfile: PersonProfile,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit = {}
) {
    ProfilePanel(
        title = personProfile.showName,
        subtitle = personProfile.signature,
        introduction = "ID: ${personProfile.id}",
        avatarUrl = personProfile.faceUrl,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun ProfilePanel(
    groupProfile: GroupProfile,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit = {}
) {
    ProfilePanel(
        title = groupProfile.name,
        subtitle = groupProfile.introduction,
        introduction = "GroupID: ${groupProfile.id}\nCreateTime: ${groupProfile.createTimeFormat}\nMemberCount: ${groupProfile.memberCount}",
        avatarUrl = groupProfile.faceUrl,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun ProfilePanel(
    title: String,
    subtitle: String,
    introduction: String,
    avatarUrl: String,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    val navHostController = LocalNavHostController.current
    ConstraintLayout(modifier = Modifier.padding(paddingValues = contentPadding)) {
        val (titleRefs, subtitleRefs, introductionRefs, backgroundRefs, avatarRefs, contentRefs) = createRefs()
        BezierImage(modifier = Modifier
            .constrainAs(ref = backgroundRefs) {
                start.linkTo(anchor = parent.start)
                end.linkTo(anchor = parent.end)
                top.linkTo(anchor = parent.top)
            }
            .aspectRatio(ratio = 5f / 4f)
            .zIndex(zIndex = -100f)
            .scrim(color = Color(0x51444B53)),
            data = avatarUrl)
        BouncyImage(
            modifier = Modifier
                .constrainAs(ref = avatarRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    bottom.linkTo(anchor = backgroundRefs.bottom)
                }
                .size(size = 100.dp)
                .clickable {
                    if (avatarUrl.isNotBlank()) {
                        navHostController.navToPreviewImagePage(imagePath = avatarUrl)
                    }
                },
            data = avatarUrl
        )
        Text(text = title,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .constrainAs(ref = titleRefs) {
                    start.linkTo(anchor = backgroundRefs.start)
                    end.linkTo(anchor = backgroundRefs.end)
                    top.linkTo(anchor = parent.top)
                }
                .statusBarsPadding()
                .padding(start = 10.dp, end = 10.dp, top = 30.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            color = Color.White,
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
            fontFamily = FontFamily.Serif,
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