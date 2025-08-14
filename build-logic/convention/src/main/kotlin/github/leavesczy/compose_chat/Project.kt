package github.leavesczy.compose_chat

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun Project.configureAndroidProject() {
    val commonExtension =
        extensions.findByType<ApplicationExtension>() ?: extensions.findByType<LibraryExtension>()!!
    commonExtension.apply {
        compileSdk = 36
        buildToolsVersion = "36.0.0"
        defaultConfig {
            minSdk = 23
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_18
            targetCompatibility = JavaVersion.VERSION_18
        }
        lint {
            checkDependencies = true
        }
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_18
        }
    }
    dependencies {
        add("testImplementation", libs.findLibrary("junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-espresso").get())
    }
}