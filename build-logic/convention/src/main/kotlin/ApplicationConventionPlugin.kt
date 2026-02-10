import com.android.build.api.dsl.ApplicationExtension
import github.leavesczy.compose_chat.configureAndroidApplication
import github.leavesczy.compose_chat.configureAndroidProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class ApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.android.application")
            val commonExtension = extensions.getByType(type = ApplicationExtension::class)
            configureAndroidApplication(commonExtension = commonExtension)
            configureAndroidProject(commonExtension = commonExtension)
        }
    }

}