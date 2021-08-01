package github.leavesc.compose_chat.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * @Author: leavesC
 * @Date: 2021/7/3 18:57
 * @Desc:
 * @Github：https://github.com/leavesC
 */

@Composable
fun SetSystemBarsColor(
    key: Any = Unit,
    statusBarColor: Color = MaterialTheme.colors.background,
    navigationBarColor: Color = MaterialTheme.colors.background
) {
    val systemUiController = rememberSystemUiController()
    val isLight = MaterialTheme.colors.isLight
    DisposableEffect(key1 = key) {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = isLight
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = isLight
        )
        systemUiController.systemBarsDarkContentEnabled = isLight
        onDispose {

        }
    }
}

@Composable
fun CommonDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier, thickness = 0.3.dp,
    )
}

@SuppressLint("ModifierParameter")
@Composable
fun CommonButton(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp), text: String, onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            onClick()
        }) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

@Composable
fun EmptyView() {
    Text(
        text = "Empty",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 49.sp,
    )
}

fun Modifier.scrim(colors: List<Color>): Modifier = drawWithContent {
    drawContent()
    drawRect(Brush.verticalGradient(colors))
}