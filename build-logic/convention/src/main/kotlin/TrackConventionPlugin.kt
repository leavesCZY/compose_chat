import github.leavesczy.track.composeclick.ComposeClickTrackPluginParameter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class TrackConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "io.github.leavesczy.track")
            extensions.configure<ComposeClickTrackPluginParameter> {
                clickWrapperClass = "github.leavesczy.compose_chat.extensions.ComposeOnClick"
                skipOnClickLabel = "notCheck"
            }
        }
    }

}