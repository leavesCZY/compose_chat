import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "github.leavesczy.compose_chat.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.leavesczy.track.gradle)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.chat.android.application.get().pluginId
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.chat.android.library.get().pluginId
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.chat.android.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("kotlinParcelize") {
            id = libs.plugins.chat.kotlin.parcelize.get().pluginId
            implementationClass = "ParcelizeConventionPlugin"
        }
        register("leavesczyTrack") {
            id = libs.plugins.chat.leavesczy.track.get().pluginId
            implementationClass = "TrackConventionPlugin"
        }
    }
}