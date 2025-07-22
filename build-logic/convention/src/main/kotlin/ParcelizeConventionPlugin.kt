import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:09
 * @Desc:
 */
class ParcelizeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.parcelize")
        }
    }

}