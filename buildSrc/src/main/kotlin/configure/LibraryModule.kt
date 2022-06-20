import com.android.build.gradle.LibraryExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.io.File

/**
 * @Author: CZY
 * @Date: 2022/5/29 17:28
 * @Desc:
 */
internal fun LibraryExtension.libraryModule() {
    compileSdk = BuildConfig.compileSdk
    buildToolsVersion = BuildConfig.buildToolsVersion
    defaultConfig {
        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }
    compileOptions {
        sourceCompatibility = BuildConfig.sourceCompatibility
        targetCompatibility = BuildConfig.targetCompatibility
    }
    ((this as ExtensionAware).extensions.getByName("kotlinOptions") as KotlinJvmOptions).apply {
        jvmTarget = BuildConfig.jvmTarget
    }
}