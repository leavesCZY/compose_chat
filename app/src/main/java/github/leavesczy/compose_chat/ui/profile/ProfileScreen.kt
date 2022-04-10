package github.leavesczy.compose_chat.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToPreviewImageScreen
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.widgets.BezierImage
import github.leavesczy.compose_chat.ui.widgets.BouncyImage

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(
        personProfile = PersonProfile(
            userId = "公众号：字节数组",
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
fun ProfileScreen(
    title: String,
    subtitle: String,
    introduction: String,
    avatarUrl: String,
    content: @Composable () -> Unit
) {
    val navHostController = LocalNavHostController.current
    ConstraintLayout {
        val (titleRefs, subtitleRefs, introductionRefs, backgroundRefs, avatarRefs, contentRefs) = createRefs()
        BezierImage(modifier = Modifier
            .constrainAs(ref = backgroundRefs) {
                start.linkTo(anchor = parent.start)
                end.linkTo(anchor = parent.end)
                top.linkTo(anchor = parent.top)
            }
            .aspectRatio(ratio = 5f / 4f)
            .zIndex(zIndex = -100f)
            .scrim(colors = listOf(Color(color = 0x51444B53), Color(color = 0x418D8282))),
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
                        navHostController.navToPreviewImageScreen(imagePath = avatarUrl)
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