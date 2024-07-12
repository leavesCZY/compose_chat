import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "github.leavesczy.compose_chat.build.logic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
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
            id = "chat.android.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "chat.android.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "chat.android.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("kotlinParcelize") {
            id = "chat.kotlin.parcelize"
            implementationClass = "ParcelizeConventionPlugin"
        }
        register("leavesczyTrack") {
            id = "chat.leavesczy.track"
            implementationClass = "TrackConventionPlugin"
        }
    }
}