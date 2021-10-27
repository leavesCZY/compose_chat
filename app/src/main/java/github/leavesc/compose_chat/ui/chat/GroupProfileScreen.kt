package github.leavesc.compose_chat.ui.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.base.model.GroupMemberProfile
import github.leavesc.compose_chat.base.model.GroupProfile
import github.leavesc.compose_chat.extend.scrim
import github.leavesc.compose_chat.logic.GroupProfileViewModel
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.theme.BezierShape
import github.leavesc.compose_chat.ui.weigets.CoilImage
import github.leavesc.compose_chat.ui.weigets.CommonDivider
import github.leavesc.compose_chat.ui.weigets.OutlinedAvatar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @Author: leavesC
 * @Date: 2021/10/27 18:04
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Preview
@Composable
private fun PreviewGroupProfileScreen() {
    GroupProfileScreen(groupProfile = GroupProfile.Empty)
}

@Composable
fun GroupProfileScreen(navController: NavHostController, groupId: String) {
    val groupProfileViewModel = viewModel<GroupProfileViewModel>(factory = object :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupProfileViewModel(groupId = groupId) as T
        }
    })
    val groupProfileScreenState by groupProfileViewModel.groupProfileScreenState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val onClickMember: (GroupMemberProfile) -> Unit = remember {
            object : (GroupMemberProfile) -> Unit {
                override fun invoke(member: GroupMemberProfile) {
                    navController.navigate(
                        route = Screen.FriendProfileScreen.generateRoute(friendId = member.userId)
                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item(key = true) {
                GroupProfileScreen(groupProfile = groupProfileScreenState.groupProfile)
            }
            val memberList = groupProfileScreenState.memberList
            memberList.forEach {
                item(key = it.userId) {
                    GroupMemberItem(groupMemberProfile = it, onClickMember = onClickMember)
                }
            }
        }
    }
}

@Composable
private fun GroupProfileScreen(groupProfile: GroupProfile) {
    ConstraintLayout(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .zIndex(zIndex = 1f)
    ) {
        val groupFaceUrl = groupProfile.faceUrl
        val groupId = groupProfile.id
        val groupName = groupProfile.name
        val groupIntroduction = groupProfile.introduction
        val groupCreateTime = groupProfile.createTimeFormat
        val memberCount = groupProfile.memberCount
        val animateValue by rememberInfiniteTransition().animateFloat(
            initialValue = 1.3f, targetValue = 1.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
        val (backgroundRefs, idRefs, faceRefs, nameRefs, notificationRefs) = createRefs()
        CoilImage(
            data = groupFaceUrl,
            modifier = Modifier
                .constrainAs(ref = backgroundRefs) {

                }
                .fillMaxWidth()
                .aspectRatio(ratio = 5f / 4f)
                .clip(shape = BezierShape(padding = animateValue * 100))
                .scrim(colors = listOf(Color(0x40000000), Color(0x40F4F4F4)))
                .scale(scale = animateValue)
                .rotate(degrees = animateValue * 10.3f)
        )
        val avatarSize = 90.dp
        val coroutineScope = rememberCoroutineScope()
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        OutlinedAvatar(
            data = groupFaceUrl,
            modifier = Modifier
                .constrainAs(ref = faceRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    bottom.linkTo(backgroundRefs.bottom)
                }
                .size(size = avatarSize)
                .zIndex(zIndex = 1f)
                .offset {
                    IntOffset(
                        x = offsetX.roundToInt(),
                        y = offsetY.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {

                        },
                        onDragCancel = {

                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                Animatable(
                                    initialValue = Offset(offsetX, offsetY),
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
                        },
                        onDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        },
                    )
                }
        )
        Text(text = groupName,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = nameRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    top.linkTo(parent.top)
                }
                .statusBarsPadding()
                .padding(start = 10.dp, end = 10.dp, top = 30.dp))
        Text(
            text = groupIntroduction,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = notificationRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    top.linkTo(nameRefs.bottom)
                }
                .padding(
                    start = 15.dp, end = 15.dp, top = 8.dp
                ))
        Text(
            text = "GroupID: $groupId\nCreateTime: $groupCreateTime\nMemberCount: $memberCount",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(ref = idRefs) {
                    start.linkTo(backgroundRefs.start)
                    end.linkTo(backgroundRefs.end)
                    top.linkTo(backgroundRefs.bottom)
                }
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
        )
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    onClickMember: (GroupMemberProfile) -> Unit
) {
    val padding = 12.dp
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClickMember(groupMemberProfile)
            },
    ) {
        val (avatar, showName, role, divider) = createRefs()
        CoilImage(
            data = groupMemberProfile.faceUrl,
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
            text = groupMemberProfile.showName + "（ID: ${groupMemberProfile.userId}）",
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
            text = groupMemberProfile.role + " - joinTime: ${groupMemberProfile.joinTimeFormat}",
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = role) {
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