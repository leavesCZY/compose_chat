import github.leavesczy.track.click.compose.ComposeClickPluginParameter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
class TrackConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "io.github.leavesczy.track")
            extensions.configure<ComposeClickPluginParameter> {
                onClickClass = "github.leavesczy.compose_chat.extensions.ComposeOnClick"
                onClickWhiteList = "notCheck"
            }
        }
    }

}