package github.leavesczy.compose_chat.ui.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import github.leavesczy.compose_chat.BuildConfig
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.model.MainPageAction
import github.leavesczy.compose_chat.model.MainPageDrawerViewState
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.BouncyImage

/**
 * @Author: leavesCZY
 * @Date: 2021/6/28 23:31
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageDrawer(
    viewState: MainPageDrawerViewState,
    mainPageAction: MainPageAction
) {
    Surface(modifier = Modifier.requiredWidth(width = 350.dp)) {
        BackHandler(enabled = viewState.drawerState.isOpen, onBack = {
            mainPageAction.changDrawerState(DrawerValue.Closed)
        })
        val personProfile = viewState.personProfile
        val faceUrl = personProfile.faceUrl
        val nickname = personProfile.nickname
        val signature = personProfile.signature
        val padding = 20.dp
        val context = LocalContext.current
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            val (avatarRef, nicknameRef, signatureRef, contentRef, aboutAuthorRef) = createRefs()
            BouncyImage(
                modifier = Modifier
                    .constrainAs(ref = avatarRef) {
                        start.linkTo(anchor = parent.start, margin = padding)
                        top.linkTo(anchor = parent.top, margin = padding / 2)
                    }
                    .size(size = 100.dp)
                    .clickableNoRipple {
                        if (faceUrl.isNotBlank()) {
                            PreviewImageActivity.navTo(context = context, imagePath = faceUrl)
                        }
                    },
                data = faceUrl
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = nicknameRef) {
                        linkTo(start = avatarRef.start, end = parent.end, endMargin = padding)
                        top.linkTo(anchor = avatarRef.bottom, margin = padding)
                        width = Dimension.fillToConstraints
                    },
                text = nickname,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = signatureRef) {
                        linkTo(start = avatarRef.start, end = parent.end, endMargin = padding)
                        top.linkTo(anchor = nicknameRef.bottom, margin = padding / 4)
                        width = Dimension.fillToConstraints
                    },
                text = signature,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleSmall
            )
            Column(modifier = Modifier
                .constrainAs(ref = contentRef) {
                    linkTo(start = parent.start, end = parent.end)
                    linkTo(
                        top = signatureRef.bottom,
                        bottom = parent.bottom,
                        topMargin = padding
                    )
                    height = Dimension.fillToConstraints
                }
            ) {
                SelectableItem(text = "个人资料", icon = Icons.Filled.Cabin, onClick = {
                    context.startActivity(Intent(context, ProfileUpdateActivity::class.java))
                })
                SelectableItem(text = "切换主题", icon = Icons.Filled.Sailing, onClick = {
                    mainPageAction.switchToNextTheme()
                })
                SelectableItem(text = "切换账号", icon = Icons.Filled.ColorLens, onClick = {
                    mainPageAction.logout()
                })
            }
            Text(
                modifier = Modifier
                    .constrainAs(ref = aboutAuthorRef) {
                        linkTo(start = parent.start, end = parent.end)
                        bottom.linkTo(anchor = parent.bottom, margin = padding)
                    }
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                text = "公众号: 字节数组" + "\n" +
                        "VersionCode: " + BuildConfig.VERSION_CODE + "\n" +
                        "VersionName: " + BuildConfig.VERSION_NAME,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                letterSpacing = 2.sp,
            )
        }
    }
}

@Composable
private fun SelectableItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .height(height = 60.dp)
            .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(size = 24.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = text,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}