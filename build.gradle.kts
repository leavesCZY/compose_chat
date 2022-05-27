plugins {
    id("com.android.application").version(BuildConfig.androidGradlePlugin).apply(false)
    id("com.android.library").version(BuildConfig.androidGradlePlugin).apply(false)
    kotlin("android").version(BuildConfig.kotlinAndroidGradlePlugin).apply(false)
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}