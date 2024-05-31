import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import github.leavesczy.compose_chat.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            val applicationExtension = extensions.findByType<ApplicationExtension>()
            if (applicationExtension != null) {
                configureCompose(commonExtension = applicationExtension)
            }
            val libraryExtension = extensions.findByType<LibraryExtension>()
            if (libraryExtension != null) {
                configureCompose(commonExtension = libraryExtension)
            }
        }
    }

}