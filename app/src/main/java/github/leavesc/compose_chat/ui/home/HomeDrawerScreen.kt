package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesc.compose_chat.BuildConfig
import github.leavesc.compose_chat.model.HomeDrawerViewState
import github.leavesc.compose_chat.ui.common.CommonButton
import github.leavesc.compose_chat.ui.profile.ProfileScreen
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/6/28 23:31
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeDrawerScreen(
    drawerState: DrawerState,
    homeDrawerViewState: HomeDrawerViewState
) {
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
    BackdropScaffold(
        scaffoldState = scaffoldState,
        backLayerBackgroundColor = MaterialTheme.colors.background,
        frontLayerBackgroundColor = MaterialTheme.colors.background,
        appBar = {

        },
        backLayerContent = {
            Column {
                ProfileScreen(
                    personProfile = homeDrawerViewState.userProfile
                )
                CommonButton(text = "个人资料") {
                    coroutineScope.launch {
                        scaffoldState.conceal()
                    }
                }
                CommonButton(text = "切换主题") {
                    homeDrawerViewState.switchToNextTheme()
                }
                CommonButton(text = "切换账号") {
                    homeDrawerViewState.logout()
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    text = "versionCode: " + BuildConfig.VERSION_CODE + "\n" +
                            "versionName: " + BuildConfig.VERSION_NAME + "\n" +
                            "buildTime: " + BuildConfig.BUILD_TIME,
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
            }
        },
        stickyFrontLayer = false,
        frontLayerContent = {
            AlterProfile(homeDrawerViewState = homeDrawerViewState)
        }
    )
}

@Composable
private fun AlterProfile(homeDrawerViewState: HomeDrawerViewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var faceUrl by remember(key1 = homeDrawerViewState) {
            mutableStateOf(
                homeDrawerViewState.userProfile.faceUrl
            )
        }
        var nickname by remember(key1 = homeDrawerViewState) {
            mutableStateOf(
                homeDrawerViewState.userProfile.nickname
            )
        }
        var signature by remember(key1 = homeDrawerViewState) {
            mutableStateOf(
                homeDrawerViewState.userProfile.signature
            )
        }
        val textFieldModifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 5.dp
            )
        OutlinedTextField(
            modifier = textFieldModifier,
            textStyle = MaterialTheme.typography.subtitle1,
            value = faceUrl,
            maxLines = 4,
            label = {
                Text(text = "faceUrl")
            },
            onValueChange = { faceUrl = it },
        )
        OutlinedTextField(
            modifier = textFieldModifier,
            textStyle = MaterialTheme.typography.subtitle1,
            value = nickname,
            onValueChange = {
                if (it.length > 16) {
                    return@OutlinedTextField
                }
                nickname = it
            },
            label = {
                Text(text = "nickname")
            }
        )
        OutlinedTextField(
            modifier = textFieldModifier,
            textStyle = MaterialTheme.typography.subtitle1,
            value = signature,
            onValueChange = {
                if (it.length > 50) {
                    return@OutlinedTextField
                }
                signature = it
            },
            label = {
                Text(text = "signature")
            }
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
            homeDrawerViewState.updateProfile(
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
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201404%2F26%2F20140426175610_8xZJQ.thumb.700_0.jpeg&refer=http%3A%2F%2Fcdn.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628755923&t=c7e61e8440d4b80b8a08d82244617cab",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201409%2F27%2F20140927125935_fyJe8.thumb.700_0.jpeg&refer=http%3A%2F%2Fcdn.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628954741&t=a2464b247c8d94928273013a3b1812b1",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp3.itc.cn%2Fq_70%2Fimages03%2F20200702%2F55ca503c8ac74f73a28a3d2577dd5a09.jpeg&refer=http%3A%2F%2Fp3.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628954805&t=fd97cffe36dcd422394b3b68124eb2f7",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201508%2F15%2F20150815131712_fEyPM.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628954858&t=c1a583f9b1023ee5ce915584b24c4515",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201902%2F23%2F20190223023859_gveux.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628954909&t=da57e2b024fc6476c2dd10b845b77564",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic3.zhimg.com%2F50%2Fv2-7d4869aa8ff5a92d448f9caed163c613_hd.jpg&refer=http%3A%2F%2Fpic3.zhimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955122&t=58cff21d8a19cc74868de66c1bca3cfc",
    "https://img2.baidu.com/it/u=2226359662,2129159393&fm=26&fmt=auto&gp=0.jpg",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fgss2.bdstatic.com%2F9fo3dSag_xI4khGkpoWK1HF6hhy%2Fbaike%2Fw%3D400%2Fsign%3D9bf29dff6f2762d0803ea5bf90ed0849%2Fd52a2834349b033b69abde0619ce36d3d439bdce.jpg&refer=http%3A%2F%2Fgss2.bdstatic.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955317&t=f79a3eeed10511d5933e43c11ddf55f9",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201808%2F10%2F20180810233135_fxgiy.thumb.700_0.jpg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955338&t=0ff942d38d68adc62dac60aa08854061",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg3.doubanio.com%2Fview%2Fphoto%2Fphoto%2Fpublic%2Fp1655461834.jpg&refer=http%3A%2F%2Fimg3.doubanio.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955420&t=cabbbcf19bc098505009f2c8f2f3437d",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201901%2F20%2F20190120173701_ajU3i.thumb.400_0.png&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955445&t=12e7392cc6ce14d8ef8206fd2a79b336",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.yxbao.com%2Fpatch%2Fimage%2F202003%2F11%2F4d19376251.jpg&refer=http%3A%2F%2Fimg.yxbao.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628955471&t=555c0086c039cde83c08d1ff1eeff46d"
)