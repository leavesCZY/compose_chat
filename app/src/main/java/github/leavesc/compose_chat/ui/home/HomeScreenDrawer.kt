package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.BuildConfig
import github.leavesc.compose_chat.common.SelectPictureContract
import github.leavesc.compose_chat.model.HomeScreenDrawerState
import github.leavesc.compose_chat.ui.weigets.CircleBorderCoilImage
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import github.leavesc.compose_chat.utils.showToast
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
    BackHandler(enabled = drawerState.isOpen, onBack = {
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

    LaunchedEffect(key1 = Unit) {
        snapshotFlow {
            !drawerState.isOpen && scaffoldState.isConcealed
        }.collect {
            scaffoldState.reveal()
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
            val userProfile = homeScreenDrawerState.userProfile
            val faceUrl = userProfile.faceUrl
            val nickname = userProfile.nickname
            val signature = userProfile.signature
            val padding = 20.dp
            ConstraintLayout(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.background)
            ) {
                val (avatarRefs, nicknameRefs, signatureRefs, contentRefs) = createRefs()
                CircleBorderCoilImage(
                    modifier = Modifier
                        .constrainAs(ref = avatarRefs) {
                            start.linkTo(anchor = parent.start, margin = padding)
                            top.linkTo(anchor = parent.top)
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
                        },
                    text = nickname,
                    style = MaterialTheme.typography.subtitle2
                )
                Text(
                    modifier = Modifier
                        .constrainAs(ref = signatureRefs) {
                            start.linkTo(anchor = nicknameRefs.start)
                            top.linkTo(anchor = nicknameRefs.bottom, margin = padding)
                        },
                    text = signature,
                    style = MaterialTheme.typography.subtitle2
                )
                Column(modifier = Modifier
                    .constrainAs(ref = contentRefs) {
                        start.linkTo(anchor = parent.start)
                        top.linkTo(anchor = signatureRefs.bottom, margin = padding)
                    }) {
                    Item(text = "个人资料", icon = Icons.Filled.Favorite, onClick = {
                        coroutineScope.launch {
                            scaffoldState.conceal()
                        }
                    })
                    Item(text = "切换主题", icon = Icons.Filled.TheaterComedy, onClick = {
                        coroutineScope.launch {
                            homeScreenDrawerState.switchToNextTheme()
                        }
                    })
                    Item(text = "切换账号", icon = Icons.Filled.Sailing, onClick = {
                        homeScreenDrawerState.logout()
                    })
                    Item(text = "关于", icon = Icons.Filled.Cabin, onClick = {

                    })
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                        text = "VersionCode: " + BuildConfig.VERSION_CODE + "\n" +
                                "VersionName: " + BuildConfig.VERSION_NAME + "\n" +
                                "BuildTime: " + BuildConfig.BUILD_TIME + "\n" +
                                "公众号: 字节数组",
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
private fun Item(text: String, icon: ImageVector, onClick: () -> Unit) {
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
            modifier = Modifier.size(size = 28.dp),
            imageVector = icon,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 20.dp),
            text = text
        )
    }
}

@Composable
private fun DrawerFrontLayer(
    backdropScaffoldState: BackdropScaffoldState,
    homeScreenDrawerState: HomeScreenDrawerState
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
    val coroutineScope = rememberCoroutineScope()
    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = SelectPictureContract()
    ) { imageUri ->
        if (imageUri != null) {
            coroutineScope.launch {
                val imageUrl = homeScreenDrawerState.uploadImage(imageUri)
                if (imageUrl.isBlank()) {
                    showToast("头像上传失败")
                } else {
                    faceUrl = imageUrl
                    showToast("头像已上传")
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            text = "选择头像"
        ) {
            selectPictureLauncher.launch(Unit)
        }
        CommonButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "随机头像"
        ) {
            faceUrl = randomFaceUrl()
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

fun randomFaceUrl(): String {
    return faceUrlList.random()
}

private val faceUrlList = listOf(
    "https://c-ssl.duitang.com/uploads/item/202003/12/20200312132646_tzVPE.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201908/12/20190812000052_uvtvy.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202105/10/20210510204719_37e80.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201911/26/20191126074916_ECsWi.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202111/15/20211115195445_2b112.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804235247_739b8.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804235244_d1f29.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201908/10/20190810162429_szk8M.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202012/15/20201215084659_81632.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/item/201909/27/20190927010825_KHUck.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202101/13/20210113111519_a66f7.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202105/21/20210521140708_76c83.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/item/202005/05/20200505064350_szajv.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202111/13/20211113174039_02fd1.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202104/25/20210425100448_8f5bc.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804234736_c0132.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202102/15/20210215071709_3816f.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202107/16/20210716213740_fa1b9.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202107/19/20210719025556_0414d.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202001/21/20200121093711_PF5zG.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202002/08/20200208084153_nvhRY.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202005/17/20200517102703_hlmuv.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202107/04/20210704194338_f7613.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202008/10/20200810073934_nntwx.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202006/01/20200601160249_AcRKF.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201910/20/20191020231818_mgyvc.jpg",
    "https://c-ssl.duitang.com/uploads/item/201910/20/20191020231819_fofwb.jpg",
    "https://c-ssl.duitang.com/uploads/item/202001/21/20200121093710_XeCcG.jpeg",
    "https://img1.doubanio.com/view/group_topic/l/public/p241325768.webp",
    "https://img2.doubanio.com/view/group_topic/l/public/p241325783.webp",
    "https://img9.doubanio.com/view/group_topic/l/public/p241325785.webp"
)