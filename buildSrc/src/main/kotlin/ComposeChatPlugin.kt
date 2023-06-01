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
        val projectNname = project.name
        if (projectNname == "app") {
            project.apply {
                plugin("com.android.application")
                plugin("org.jetbrains.kotlin.android")
            }
        } else {
            project.apply {
                plugin("com.android.library")
                plugin("org.jetbrains.kotlin.android")
            }
            if (projectNname == "base") {
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

}