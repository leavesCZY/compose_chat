package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.R

@Composable
fun AppIcon(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier
            .background(color = Color(0xFF073042))
            .wrapContentSize(
                align = Alignment.Center
            )
            .size(size = 400.dp),
        painter = painterResource(id = R.drawable.icon_app),
        tint = Color(0xFF3DDC84),
        contentDescription = null
    )
}

@Preview
@Composable
fun AppIcon() {
    Box(modifier = Modifier.background(color = Color.Transparent)) {
        AppIcon(modifier = Modifier)
    }
}

@Preview
@Composable
fun AppIconRound() {
    AppIcon(
        modifier = Modifier.clip(shape = CircleShape)
    )
}