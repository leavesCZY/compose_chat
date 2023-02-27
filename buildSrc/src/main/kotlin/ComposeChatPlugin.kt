import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import configure.appModule
import configure.libraryModule
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ComposeChatPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project.isAppModule()) {
            project.apply {
                plugin("com.android.application")
                plugin("org.jetbrains.kotlin.android")
            }
        } else {
            project.apply {
                plugin("com.android.library")
                plugin("org.jetbrains.kotlin.android")
            }
            if (project.name == "base") {
                project.apply {
                    plugin("kotlin-parcelize")
                }
            }
        }
        when (val androidExtension = project.extensions.getByName("android")) {
            is BaseAppModuleExtension -> {
                androidExtension.appModule(project = project)
            }
            is LibraryExtension -> {
                androidExtension.libraryModule()
            }
        }
    }

    private fun Project.isAppModule(): Boolean {
        return project.name == "app"
    }

}