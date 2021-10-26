package github.leavesc.compose_chat.ui.weigets

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.Coil
import coil.ImageLoader
import coil.compose.rememberImagePainter
import github.leavesc.compose_chat.R

object CoilImageLoader {

    fun init(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .allowHardware(false)
            .placeholder(R.drawable.icon_logo)
            .fallback(R.drawable.icon_logo)
            .error(R.drawable.icon_logo)
            .build()
        Coil.setImageLoader(imageLoader)
    }

}

@Composable
fun CoilImage(
    modifier: Modifier = Modifier,
    data: Any,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Image(
        modifier = modifier,
        painter = rememberImagePainter(data = data),
        contentDescription = contentDescription,
        contentScale = contentScale,
    )
}

@Composable
fun OutlinedAvatar(
    modifier: Modifier = Modifier,
    data: String,
    outlineSize: Dp = 3.dp,
    outlineColor: Color = MaterialTheme.colors.primaryVariant
) {
    Box(
        modifier = modifier.background(
            color = outlineColor,
            shape = CircleShape
        )
    ) {
        CoilImage(
            data = data,
            modifier = Modifier
                .padding(outlineSize)
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}