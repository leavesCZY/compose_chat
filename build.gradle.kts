buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${BuildConfig.androidGradlePlugin}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${BuildConfig.kotlinAndroidGradlePlugin}")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}