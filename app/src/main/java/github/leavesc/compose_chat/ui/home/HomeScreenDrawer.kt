package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.BuildConfig
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.model.HomeScreenDrawerState
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.weigets.BouncyImage
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/6/28 23:31
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun HomeScreenDrawer(homeScreenDrawerState: HomeScreenDrawerState) {
    val navHostController = LocalNavHostController.current
    val drawerState = homeScreenDrawerState.drawerState
    val coroutineScope = rememberCoroutineScope()

    fun closeDrawer() {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    BackHandler(enabled = drawerState.isOpen, onBack = {
        closeDrawer()
    })
    Surface(modifier = Modifier.fillMaxSize()) {
        val userProfile = homeScreenDrawerState.userProfile
        val faceUrl = userProfile.faceUrl
        val nickname = userProfile.nickname
        val signature = userProfile.signature
        val padding = 20.dp
        ConstraintLayout {
            val (avatarRefs, nicknameRefs, signatureRefs, contentRefs) = createRefs()
            BouncyImage(
                modifier = Modifier
                    .constrainAs(ref = avatarRefs) {
                        start.linkTo(anchor = parent.start, margin = padding)
                        top.linkTo(anchor = parent.top, margin = padding)
                    }
                    .statusBarsPadding()
                    .size(size = 100.dp),
                data = faceUrl
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = nicknameRefs) {
                        start.linkTo(anchor = avatarRefs.start)
                        top.linkTo(anchor = avatarRefs.bottom, margin = padding)
                        end.linkTo(anchor = parent.end, margin = padding)
                        width = Dimension.fillToConstraints
                    },
                text = nickname,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier
                    .constrainAs(ref = signatureRefs) {
                        start.linkTo(anchor = nicknameRefs.start)
                        top.linkTo(anchor = nicknameRefs.bottom, margin = padding / 4)
                        end.linkTo(anchor = parent.end, margin = padding)
                        width = Dimension.fillToConstraints
                    },
                text = signature,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleSmall
            )
            Column(modifier = Modifier
                .constrainAs(ref = contentRefs) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = signatureRefs.bottom, margin = padding)
                }) {
                SelectableItem(text = "个人资料", icon = Icons.Filled.Cabin, onClick = {
                    navHostController.navigate(route = Screen.UpdateProfileScreen.generateRoute())
                })
                SelectableItem(text = "切换主题", icon = Icons.Filled.Sailing, onClick = {
                    homeScreenDrawerState.switchToNextTheme()
                })
                SelectableItem(text = "切换账号", icon = Icons.Filled.TheaterComedy, onClick = {
                    homeScreenDrawerState.logout()
                })
                SelectableItem(text = "关于作者", icon = Icons.Filled.Favorite, onClick = {

                })
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                        .padding(top = padding),
                    text = "VersionCode: " + BuildConfig.VERSION_CODE + "\n" +
                            "VersionName: " + BuildConfig.VERSION_NAME + "\n" +
                            "BuildTime: " + BuildConfig.BUILD_TIME + "\n" +
                            "公众号: 字节数组" + "\n" +
                            "我的微信：leavesCZY",
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                )
            }
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}