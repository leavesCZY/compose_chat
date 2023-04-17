package github.leavesczy.compose_chat.ui.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.BuildConfig
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.BouncyImage
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageDrawer(mainViewModel: MainViewModel) {
    val drawerViewState = mainViewModel.drawerViewState
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = drawerViewState.drawerState.isOpen, onBack = {
        coroutineScope.launch {
            mainViewModel.drawerViewState.drawerState.close()
        }
    })
    Surface(
        modifier = Modifier,
        color = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
    ) {
        val personProfile = drawerViewState.personProfile
        val id = personProfile.id
        val faceUrl = personProfile.faceUrl
        val nickname = personProfile.nickname
        val signature = personProfile.signature
        val padding = 18.dp
        val context = LocalContext.current
        ConstraintLayout(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth(
                    fraction = 0.90f
                )
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            val (avatarRef, userIdRef, nicknameRef, signatureRef, contentRef, aboutAuthorRef) = createRefs()
            BouncyImage(modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    start.linkTo(
                        anchor = parent.start, margin = padding
                    )
                    top.linkTo(
                        anchor = parent.top, margin = padding / 2
                    )
                }
                .size(size = 90.dp)
                .clickableNoRipple {
                    if (faceUrl.isNotBlank()) {
                        PreviewImageActivity.navTo(
                            context = context, imagePath = faceUrl
                        )
                    }
                }, data = faceUrl
            )
            Text(
                modifier = Modifier.constrainAs(ref = userIdRef) {
                    linkTo(
                        start = avatarRef.start, end = parent.end, endMargin = padding
                    )
                    top.linkTo(
                        anchor = avatarRef.bottom, margin = padding
                    )
                    width = Dimension.fillToConstraints
                }, text = id, fontSize = 18.sp
            )
            Text(
                modifier = Modifier.constrainAs(ref = nicknameRef) {
                    linkTo(
                        start = avatarRef.start, end = parent.end, endMargin = padding
                    )
                    top.linkTo(
                        anchor = userIdRef.bottom, margin = padding / 4
                    )
                    width = Dimension.fillToConstraints
                }, text = nickname, fontSize = 14.sp
            )
            Text(
                modifier = Modifier.constrainAs(ref = signatureRef) {
                    linkTo(
                        start = avatarRef.start, end = parent.end, endMargin = padding
                    )
                    top.linkTo(
                        anchor = nicknameRef.bottom, margin = padding / 4
                    )
                    width = Dimension.fillToConstraints
                }, text = signature, fontSize = 14.sp
            )
            Column(modifier = Modifier.constrainAs(ref = contentRef) {
                linkTo(
                    start = parent.start, end = parent.end
                )
                linkTo(
                    top = signatureRef.bottom, bottom = parent.bottom, topMargin = padding
                )
                height = Dimension.fillToConstraints
            }) {
                SelectableItem(text = "个人资料", icon = Icons.Filled.Cabin, onClick = {
                    context.startActivity(
                        Intent(
                            context, ProfileUpdateActivity::class.java
                        )
                    )
                })
                SelectableItem(text = "切换主题", icon = Icons.Filled.Sailing, onClick = {
                    mainViewModel.switchToNextTheme()
                })
                SelectableItem(text = "切换账号", icon = Icons.Filled.ColorLens, onClick = {
                    mainViewModel.logout()
                })
            }
            Text(modifier = Modifier
                .constrainAs(ref = aboutAuthorRef) {
                    linkTo(
                        start = parent.start, end = parent.end
                    )
                    bottom.linkTo(
                        anchor = parent.bottom, margin = padding * 2
                    )
                }
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally),
                text = "公众号: 字节数组" + "\n" + "VersionCode: " + BuildConfig.VERSION_CODE + "\n" + "VersionName: " + BuildConfig.VERSION_NAME,
                textAlign = TextAlign.Center,
                fontSize = 15.sp)
        }
    }
}

@Composable
private fun SelectableItem(
    text: String, icon: ImageVector, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(
                height = 60.dp
            )
            .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(size = 22.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.padding(start = 10.dp), text = text, fontSize = 17.sp
        )
    }
}