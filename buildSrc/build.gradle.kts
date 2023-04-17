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

dependencies {
    val agpVersion = "8.0.0"
    val kotlinVersion = "1.8.20"
    implementation("com.android.tools.build:gradle-api:${agpVersion}")
    implementation("com.android.tools.build:gradle:${agpVersion}")
    implementation(kotlin("gradle-plugin", version = kotlinVersion))
    implementation(kotlin("gradle-plugin-api", version = kotlinVersion))
    implementation(gradleKotlinDsl())
}