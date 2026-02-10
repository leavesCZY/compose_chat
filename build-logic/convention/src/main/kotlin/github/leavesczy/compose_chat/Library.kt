package github.leavesczy.compose_chat

import com.android.build.api.dsl.LibraryExtension
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun configureAndroidLibrary(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        defaultConfig {
            consumerProguardFiles.add(File("consumer-rules.pro"))
        }
    }
}