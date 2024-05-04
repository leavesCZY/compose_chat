import github.leavesczy.trace.click.compose.ComposeClickPluginParameter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class TraceConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.github.leavesczy.trace")
            }
            extensions.configure<ComposeClickPluginParameter> {
                onClickClass = "github.leavesczy.compose_chat.extend.ComposeOnClick"
                onClickWhiteList = "notCheck"
            }
        }
    }

}