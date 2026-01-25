package github.leavesczy.compose_chat

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun Project.configureCompose(commonExtension: CommonExtension) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }
        dependencies {
            val composeBom = libs.findLibrary("androidx-compose-bom").get()
            val composeBomPlatform = platform(composeBom)
            add("implementation", composeBomPlatform)
            add("androidTestImplementation", composeBomPlatform)
            add("implementation", libs.findLibrary("androidx-compose-ui").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-util").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
            add("implementation", libs.findLibrary("androidx-compose-material-icons").get())
            add("implementation", libs.findLibrary("androidx-compose-material3").get())
        }
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            optIn.addAll(
                setOf(
                    "androidx.compose.material3.ExperimentalMaterial3Api"
                )
            )
        }
    }
}