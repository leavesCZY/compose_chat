import com.android.build.api.dsl.CommonExtension
import github.leavesczy.compose_chat.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            val commonExtension = extensions.getByType(type = CommonExtension::class)
            configureCompose(commonExtension = commonExtension)
        }
    }

}