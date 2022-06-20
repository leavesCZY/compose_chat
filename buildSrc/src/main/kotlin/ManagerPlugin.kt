import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @Author: CZY
 * @Date: 2022/5/29 14:52
 * @Desc:
 */
class ManagerPlugin : Plugin<Project> {

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
        }
        when (val androidExtension = project.extensions.getByName("android")) {
            is BaseAppModuleExtension -> {
                androidExtension.appModule(project)
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