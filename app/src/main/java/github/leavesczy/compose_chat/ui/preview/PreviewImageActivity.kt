package github.leavesczy.compose_chat.ui.preview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.SystemBarColor

/**
 * @Author: CZY
 * @Date: 2022/7/17 14:06
 * @Desc:
 */
class PreviewImageActivity : BaseActivity() {

    companion object {

        private const val keyImagePath = "keyImagePath"

        fun navTo(context: Context, imagePath: String) {
            val intent = Intent(context, PreviewImageActivity::class.java)
            intent.putExtra(keyImagePath, imagePath)
            context.startActivity(intent)
        }

    }

    private val imagePath by lazy {
        intent.getStringExtra(keyImagePath) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChatTheme {
                SystemBarColor(appTheme = AppTheme.Dark)
                PreviewImagePage(imagePath = imagePath)
            }
        }
    }

}