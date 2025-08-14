import github.leavesczy.compose_chat.configureAndroidApplication
import github.leavesczy.compose_chat.configureAndroidProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class ApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            configureAndroidApplication()
            configureAndroidProject()
        }
    }

}