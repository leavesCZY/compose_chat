package github.leavesc.compose_chat.ui.friend

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.ui.weigets.CoilImage
import github.leavesc.compose_chat.ui.weigets.CommonDivider
import github.leavesc.compose_chat.ui.weigets.EmptyView

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:55
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun FriendshipScreen(
    listState: LazyListState,
    paddingValues: PaddingValues,
    friendList: List<PersonProfile>,
    onClickFriend: (PersonProfile) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
    ) {
        if (friendList.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(state = listState) {
                friendList.forEach {
                    item(key = it.userId) {
                        FriendshipItem(personProfile = it, onClickFriend = onClickFriend)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewFriendshipItem() {
    FriendshipItem(personProfile = PersonProfile.Empty.copy(
        nickname = "业志陈", signature = "喜欢写又长又臭的文章，希望对你有所帮助 \uD83E\uDD23\uD83E\uDD23"
    ), onClickFriend = {

    })
}

@Composable
fun FriendshipItem(personProfile: PersonProfile, onClickFriend: (PersonProfile) -> Unit) {
    val padding = 12.dp
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClickFriend(personProfile)
            },
    ) {
        val (avatar, showName, signature, divider) = createRefs()
        CoilImage(
            data = personProfile.faceUrl,
            modifier = Modifier
                .padding(start = padding * 1.5f, top = padding, bottom = padding)
                .size(size = 50.dp)
                .clip(shape = CircleShape)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        Text(
            text = personProfile.showName,
            style = MaterialTheme.typography.subtitle1,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, top = padding, end = padding)
                .constrainAs(ref = showName) {
                    start.linkTo(anchor = avatar.end)
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        Text(
            text = personProfile.signature,
            style = MaterialTheme.typography.subtitle2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = signature) {
                    start.linkTo(anchor = showName.start)
                    top.linkTo(anchor = showName.bottom, margin = padding / 2)
                    end.linkTo(anchor = parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = divider) {
                    start.linkTo(anchor = avatar.end, margin = padding)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = avatar.bottom)
                    width = Dimension.fillToConstraints
                }
        )
    }
}