package github.leavesczy.compose_chat.ui.preview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.main.logic.AppTheme
import github.leavesczy.compose_chat.ui.theme.DarkColorScheme
import github.leavesczy.compose_chat.ui.widgets.SystemBarTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
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
        setContent(systemBarTheme = {
            SystemBarTheme(
                appTheme = AppTheme.Dark,
                navigationBarColor = DarkColorScheme.background
            )
        }) {
            PreviewImagePage(imagePath = imagePath)
        }
    }

}