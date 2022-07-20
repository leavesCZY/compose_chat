package github.leavesczy.compose_chat.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

/**
 * @Author: CZY
 * @Date: 2022/7/17 13:57
 * @Desc:
 */
open class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    protected inline fun <reified T : Activity> startActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }

}