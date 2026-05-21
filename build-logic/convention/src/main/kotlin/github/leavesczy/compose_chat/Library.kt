package github.leavesczy.compose_chat

import com.android.build.api.dsl.LibraryExtension
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
internal fun configureAndroidLibrary(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        defaultConfig {
            consumerProguardFiles.add(File("consumer-rules.pro"))
        }
    }
}