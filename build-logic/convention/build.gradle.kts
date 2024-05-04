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
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.leavesczy.trace.gradle)
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
        register("leavesczyTrace") {
            id = "chat.leavesczy.trace"
            implementationClass = "TraceConventionPlugin"
        }
    }
}