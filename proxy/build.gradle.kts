plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = BuildConfig.compileSdk
    buildToolsVersion = BuildConfig.buildTools
    defaultConfig {
        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }
    compileOptions {
        sourceCompatibility = BuildConfig.sourceCompatibility
        targetCompatibility = BuildConfig.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = BuildConfig.jvmTarget
    }
}

dependencies {
    testImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.testExt)
    androidTestImplementation(Dependencies.Test.espresso)
    implementation(Dependencies.Base.coroutines)
    implementation(Dependencies.Base.imSdk)
    implementation(project(":common"))
}