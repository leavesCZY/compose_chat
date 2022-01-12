package github.leavesc.compose_chat.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.base.model.GroupProfile
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.extend.navToPreviewImageScreen
import github.leavesc.compose_chat.ui.weigets.BezierImage
import github.leavesc.compose_chat.ui.weigets.BouncyImage

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
        BezierImage(modifier = Modifier.constrainAs(ref = backgroundRefs) {

        }, data = avatarUrl)
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
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
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
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
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
            fontWeight = FontWeight.Bold,
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