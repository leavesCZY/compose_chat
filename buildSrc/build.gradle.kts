import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

dependencies {
    val androidGradlePluginVersion = "7.3.1"
    val kotlinGradlePluginVersion = "1.7.20"
    implementation("com.android.tools.build:gradle-api:${androidGradlePluginVersion}")
    implementation("com.android.tools.build:gradle:${androidGradlePluginVersion}")
    implementation(kotlin("gradle-plugin", version = kotlinGradlePluginVersion))
    implementation(kotlin("gradle-plugin-api", version = kotlinGradlePluginVersion))
    implementation(gradleKotlinDsl())
}