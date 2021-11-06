package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.BuildConfig
import github.leavesc.compose_chat.model.HomeScreenDrawerState
import github.leavesc.compose_chat.ui.profile.ProfileScreen
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/6/28 23:31
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun HomeScreenDrawer(homeScreenDrawerState: HomeScreenDrawerState) {
    val drawerState = homeScreenDrawerState.drawerState
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)
    BackHandler(enabled = drawerState.isOpen || scaffoldState.isConcealed, onBack = {
        if (scaffoldState.isConcealed) {
            coroutineScope.launch {
                scaffoldState.reveal()
            }
        } else if (drawerState.isOpen) {
            coroutineScope.launch {
                drawerState.close()
            }
        }
    })

    if (drawerState.isOpen) {
        LaunchedEffect(key1 = drawerState.isOpen) {
            snapshotFlow {
                drawerState.isOpen
            }.collect {
                if (!it && scaffoldState.isConcealed) {
                    coroutineScope.launch {
                        scaffoldState.reveal()
                    }
                }
            }
        }
    }

    BackdropScaffold(
        scaffoldState = scaffoldState,
        backLayerBackgroundColor = MaterialTheme.colors.background,
        frontLayerBackgroundColor = MaterialTheme.colors.background,
        frontLayerScrimColor = MaterialTheme.colors.primary.copy(alpha = 0.60f),
        appBar = {

        },
        backLayerContent = {
            ProfileScreen(
                personProfile = homeScreenDrawerState.userProfile
            ) {
                Column {
                    CommonButton(text = "个人资料") {
                        coroutineScope.launch {
                            scaffoldState.conceal()
                        }
                    }
                    CommonButton(text = "切换主题") {
                        homeScreenDrawerState.switchToNextTheme()
                    }
                    CommonButton(text = "切换账号") {
                        homeScreenDrawerState.logout()
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                        text = "VersionCode: " + BuildConfig.VERSION_CODE + "\n" +
                                "VersionName: " + BuildConfig.VERSION_NAME + "\n" +
                                "BuildTime: " + BuildConfig.BUILD_TIME,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp,
                    )
                }
            }
        },
        stickyFrontLayer = false,
        frontLayerContent = {
            DrawerFrontLayer(
                backdropScaffoldState = scaffoldState,
                homeScreenDrawerState = homeScreenDrawerState
            )
        }
    )
}

@Composable
private fun DrawerFrontLayer(
    backdropScaffoldState: BackdropScaffoldState,
    homeScreenDrawerState: HomeScreenDrawerState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var faceUrl by remember(
            key1 = backdropScaffoldState.isRevealed,
            key2 = homeScreenDrawerState
        ) {
            mutableStateOf(
                homeScreenDrawerState.userProfile.faceUrl
            )
        }
        var nickname by remember(
            key1 = backdropScaffoldState.isRevealed,
            key2 = homeScreenDrawerState
        ) {
            mutableStateOf(
                homeScreenDrawerState.userProfile.nickname
            )
        }
        var signature by remember(
            key1 = backdropScaffoldState.isRevealed,
            key2 = homeScreenDrawerState
        ) {
            mutableStateOf(
                homeScreenDrawerState.userProfile.signature
            )
        }
        val textFieldModifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 5.dp
            )
        CommonOutlinedTextField(
            modifier = textFieldModifier,
            value = faceUrl,
            onValueChange = { faceUrl = it },
            label = "faceUrl",
        )
        CommonOutlinedTextField(
            modifier = textFieldModifier,
            value = nickname,
            onValueChange = {
                if (it.length > 16) {
                    return@CommonOutlinedTextField
                }
                nickname = it
            },
            label = "nickname"
        )
        CommonOutlinedTextField(
            modifier = textFieldModifier,
            value = signature,
            onValueChange = {
                if (it.length > 50) {
                    return@CommonOutlinedTextField
                }
                signature = it
            },
            label = "signature"
        )
        CommonButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "随机头像"
        ) {
            faceUrl = faceUrlList.random()
        }
        CommonButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "保存设置"
        ) {
            homeScreenDrawerState.updateProfile(
                faceUrl,
                nickname,
                signature
            )
        }
    }
}

private val faceUrlList = listOf(
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201810%2F26%2F20181026204411_Vi3fF.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955160&t=0cd558fdd35237aff0549b59de92619f",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201905%2F28%2F20190528143150_fETNW.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628609753&t=a6d4d6c1ab3e9f501ca970699ed6be37",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201406%2F03%2F20140603170900_MtE8Q.thumb.600_0.jpeg&refer=http%3A%2F%2Fcdn.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628609767&t=fbe267bc8acb3904b7f7185a390b8992",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201312%2F20%2F20131220165207_kjkFt.thumb.700_0.gif&refer=http%3A%2F%2Fimg5.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628755856&t=7a9c33754f0cb78f4d1bb32ea822cf5e",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201508%2F15%2F20150815131712_fEyPM.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628954858&t=c1a583f9b1023ee5ce915584b24c4515",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201902%2F23%2F20190223023859_gveux.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628954909&t=da57e2b024fc6476c2dd10b845b77564",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fgss2.bdstatic.com%2F9fo3dSag_xI4khGkpoWK1HF6hhy%2Fbaike%2Fw%3D400%2Fsign%3D9bf29dff6f2762d0803ea5bf90ed0849%2Fd52a2834349b033b69abde0619ce36d3d439bdce.jpg&refer=http%3A%2F%2Fgss2.bdstatic.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955317&t=f79a3eeed10511d5933e43c11ddf55f9",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201804%2F25%2F20180425161551_tggkk.jpg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1638277885&t=d8aa569399ceeead47f0a87942fbdb49",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F21%2F20200321115129_jwwep.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1638277919&t=9c2d9d69b155f5cd58fea2fa53a14d07",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2F2021%2Fedpic%2F54%2Ff9%2F84%2F54f984b85597c4736f72e34b84994f24_1.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1638277939&t=0db35c85df5cc6a15ca2b4ffb219601b",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201702%2F04%2F20170204222045_B2uNc.png&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1638277961&t=b321eec8040c2f842f17c17dd0f6c9c4",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic2.zhimg.com%2F50%2Fv2-1bff255a8efa7715be7f6101cde7dfcf_hd.jpg&refer=http%3A%2F%2Fpic2.zhimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1638278036&t=bb2d533785a1b54b783a2230cc8dda87",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201807%2F29%2F20180729012254_X4K2V.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1638278054&t=c933613b5c31264437353ae243c48888",
    "https://pics4.baidu.com/feed/f636afc379310a55b4cdc6df9c53d8af832610cf.jpeg?token=baa52d15ac793038ea530fb416e22eb1",
    "https://pics1.baidu.com/feed/a50f4bfbfbedab6430800ab184be73c578311e5b.jpeg?token=ddb91150a5188515f9596d5ff3435e9f",
    "https://img2.baidu.com/it/u=868101410,3287615144&fm=26&fmt=auto",
    "https://img0.baidu.com/it/u=204135308,1673897159&fm=26&fmt=auto",
    "https://img2.baidu.com/it/u=2831871928,2384087385&fm=26&fmt=auto",
    "https://img0.baidu.com/it/u=2989319357,4058742052&fm=26&fmt=auto",
)