repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    val agpVersion = "8.1.0"
    val kotlinVersion = "1.9.0"
    val traceVersion = "0.0.2"
    implementation("com.android.tools.build:gradle-api:${agpVersion}")
    implementation("com.android.tools.build:gradle:${agpVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:${kotlinVersion}")
    implementation("io.github.leavesczy:trace:${traceVersion}")
}