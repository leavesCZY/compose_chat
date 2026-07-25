package github.leavesczy.compose_chat.ui.main.person

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.extensions.clickableNoRipple
import github.leavesczy.compose_chat.extensions.scrim
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.main.person.logic.PersonProfilePageViewState
import github.leavesczy.compose_chat.widgets.AnimateBouncyImage
import github.leavesczy.compose_chat.widgets.BezierImage

@Composable
fun PersonProfilePage(pageViewState: PersonProfilePageViewState) {
    val personProfile = pageViewState.personProfile
    val avatarUrl = personProfile.avatarUrl
    val title = personProfile.showName
    val subtitle = personProfile.signature
    val introduction = stringResource(id = R.string.person_profile_id, personProfile.id)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colorScheme.c_FFEAF2FF_FF0B1220.color)
    ) {
        BezierImage(
            modifier = Modifier
                .aspectRatio(ratio = 1f)
                .scrim(color = AppTheme.colorScheme.c_33000000_33000000.color),
            model = avatarUrl
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
                        pageViewState.onClickPreviewImage(avatarUrl)
                    },
                model = avatarUrl
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = title,
                fontSize = 20.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                color = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = subtitle,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                color = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = introduction,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                color = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            )
        }
    }
}