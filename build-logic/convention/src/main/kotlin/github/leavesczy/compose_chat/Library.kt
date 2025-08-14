package github.leavesczy.compose_chat

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun Project.configureAndroidLibrary() {
    extensions.configure<LibraryExtension> {
        defaultConfig {
            consumerProguardFiles.add(File("consumer-rules.pro"))
        }
    }
}