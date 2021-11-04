package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.R
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.ui.weigets.CommonDivider

/**
 * @Author: leavesC
 * @Date: 2021/6/24 16:44
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun HomeScreenTopBar(
    screenSelected: HomeScreenTab,
    openDrawer: () -> Unit,
    onAddFriend: () -> Unit,
    onJoinGroup: () -> Unit,
) {
    if (screenSelected == HomeScreenTab.Person) {
        return
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        modifier = Modifier
            .background(color = MaterialTheme.colors.primaryVariant)
            .statusBarsPadding(),
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        elevation = 0.dp,
        contentPadding = PaddingValues(
            all = 0.dp
        ),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (drawerIcon, appLogo, menuIcon, menu, divider) = createRefs()
            Icon(
                modifier = Modifier
                    .constrainAs(ref = drawerIcon) {
                        start.linkTo(anchor = parent.start)
                        top.linkTo(anchor = parent.top)
                        bottom.linkTo(anchor = parent.bottom)
                    }
                    .size(size = 28.dp)
                    .clickable(onClick = openDrawer),
                imageVector = Icons.Filled.Menu,
                contentDescription = null,
                tint = MaterialTheme.colors.surface
            )
            Icon(
                modifier = Modifier
                    .constrainAs(ref = appLogo) {
                        start.linkTo(anchor = drawerIcon.end)
                        top.linkTo(anchor = drawerIcon.top)
                        bottom.linkTo(anchor = drawerIcon.bottom)
                    }
                    .padding(start = 12.dp)
                    .size(size = 28.dp)
                    .clickable {
                        openDrawer()
                    },
                painter = painterResource(id = R.drawable.ic_crane_logo),
                contentDescription = null,
                tint = MaterialTheme.colors.surface
            )
            Icon(
                modifier = Modifier
                    .constrainAs(ref = menuIcon) {
                        end.linkTo(anchor = parent.end)
                        top.linkTo(anchor = appLogo.top)
                        bottom.linkTo(anchor = appLogo.bottom)
                    }
                    .padding(end = 12.dp)
                    .size(size = 28.dp)
                    .clickable {
                        menuExpanded = true
                    },
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colors.surface
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.TopEnd)
                    .padding(end = 20.dp)
                    .constrainAs(ref = menu) {
                        top.linkTo(anchor = menuIcon.bottom)
                    }
            ) {
                DropdownMenu(
                    modifier = Modifier.background(color = MaterialTheme.colors.background),
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {
                    DropdownMenuItem(onClick = {
                        menuExpanded = false
                        onAddFriend()
                    }) {
                        Text(text = "添加好友")
                    }
                    DropdownMenuItem(onClick = {
                        menuExpanded = false
                        onJoinGroup()
                    }) {
                        Text(text = "加入群聊")
                    }
                }
            }
            CommonDivider(modifier = Modifier
                .constrainAs(ref = divider) {
                    start.linkTo(anchor = parent.start)
                    end.linkTo(anchor = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                })
        }
    }
}