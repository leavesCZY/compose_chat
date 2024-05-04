package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import github.leavesczy.matisse.ImageEngine
import github.leavesczy.matisse.MediaResource
import kotlinx.parcelize.Parcelize

/**
 * @Author: leavesCZY
 * @Desc:
 */
@Parcelize
class MatisseImageEngine : ImageEngine {

    @Composable
    override fun Thumbnail(mediaResource: MediaResource) {
        ComponentImage(
            modifier = Modifier.fillMaxSize(),
            model = mediaResource.uri,
            contentScale = ContentScale.Crop,
            contentDescription = mediaResource.name
        )
    }

    @Composable
    override fun Image(mediaResource: MediaResource) {
        if (mediaResource.isVideo) {
            ComponentImage(
                modifier = Modifier.fillMaxWidth(),
                model = mediaResource.uri,
                contentScale = ContentScale.Fit,
                contentDescription = mediaResource.name
            )
        } else {
            ZoomableComponentImage(
                modifier = Modifier.fillMaxSize(),
                model = mediaResource.uri,
                contentScale = ContentScale.Fit,
                contentDescription = mediaResource.name
            )
        }
    }

}