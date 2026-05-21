import com.android.build.api.dsl.LibraryExtension
import github.leavesczy.compose_chat.configureAndroidLibrary
import github.leavesczy.compose_chat.configureAndroidProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.android.library")
            val libraryExtension = extensions.getByType<LibraryExtension>()
            configureAndroidProject(commonExtension = libraryExtension)
            configureAndroidLibrary(libraryExtension = libraryExtension)
        }
    }

}