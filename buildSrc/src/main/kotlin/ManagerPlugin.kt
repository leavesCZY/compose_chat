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
        when (val androidExtension = project.extensions.getByName("android")) {
            is BaseAppModuleExtension -> {
                androidExtension.app(project)
                println("ManagerPlugin->   BaseAppModuleExtension")
            }
            is LibraryExtension -> {
                androidExtension.library()
                println("ManagerPlugin->   LibraryExtension")
            }
        }
    }

//    internal fun Project.configureDependencies() {
//        DependencyHandlerScope.of(dependencies).apply {
//            implementation(Dependencies.Base.coroutines)
//        }
//        dependencies.apply {
//            add("implementation", Dependencies.Base.coroutines)
//        }
//    }
}