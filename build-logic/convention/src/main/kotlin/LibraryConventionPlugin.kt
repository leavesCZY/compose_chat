import com.android.build.gradle.LibraryExtension
import github.leavesczy.compose_chat.configureAndroidLibrary
import github.leavesczy.compose_chat.configureAndroidProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            extensions.configure<LibraryExtension> {
                configureAndroidLibrary(commonExtension = this)
                configureAndroidProject(commonExtension = this)
            }
        }
    }

}