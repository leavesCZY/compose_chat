package github.leavesc.compose_chat.ui.common

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.Coil
import coil.ImageLoader
import coil.compose.rememberImagePainter
import github.leavesc.compose_chat.R

object CoilImageLoader {

    fun init(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .allowHardware(true)
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