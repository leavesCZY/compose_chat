package github.leavesc.compose_chat.ui.common

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.Coil
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.imageLoader
import github.leavesc.compose_chat.R

object CoilImageLoader {

    fun initImageLoader(context: Context) {
        Coil.setImageLoader {
            ImageLoader.Builder(context)
                .crossfade(false)
                .allowHardware(true)
                .placeholder(R.drawable.icon_logo)
                .fallback(R.drawable.icon_logo)
                .error(R.drawable.icon_logo)
                .build().apply {
                    Coil.setImageLoader(this)
                }
        }
    }

}

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    data: Any,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val imagePainter = rememberImagePainter(
        data = data,
        imageLoader = LocalContext.current.imageLoader,
        builder = {
            placeholder(R.drawable.icon_logo)
        }
    )
    Image(
        modifier = modifier,
        painter = imagePainter,
        contentDescription = contentDescription,
        contentScale = contentScale,
    )
}