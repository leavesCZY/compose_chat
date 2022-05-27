plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = BuildConfig.compileSdk
    buildToolsVersion = BuildConfig.buildTools
    defaultConfig {
        applicationId = BuildConfig.applicationId
        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.add("zh")
        vectorDrawables {
            useSupportLibrary = true
        }
        applicationVariants.forEach { variant ->
            variant.outputs.all {
                if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                    outputFileName =
                        "chat_${variant.name}_versionCode_${variant.versionCode}_versionName_${variant.versionName}_${Function.getFormattedTime()}.apk"
                }
            }
        }
        buildConfigField("String", "VERSION_NAME", "\"${BuildConfig.versionName}\"")
    }
    signingConfigs {
        create("release") {
            storeFile = File(rootDir.absolutePath + File.separator + "key.jks")
            keyAlias = BuildConfig.keyAlias
            storePassword = BuildConfig.keyPassword
            keyPassword = BuildConfig.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.apply {
                    add("arm64-v8a")
                    add("x86")
                }
            }
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.add("arm64-v8a")
            }
        }

    }
    compileOptions {
        sourceCompatibility = BuildConfig.sourceCompatibility
        targetCompatibility = BuildConfig.targetCompatibility
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.composeVersion
    }
    kotlinOptions {
        jvmTarget = BuildConfig.jvmTarget
        freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
            addAll(
                listOf(
                    "-Xjvm-default=all",
                    "-Xallow-jvm-ir-dependencies",
                    "-Xskip-prerelease-check",
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
                    "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                    "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-Xopt-in=com.google.accompanist.insets.ExperimentalAnimatedInsets",
                    "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi",
                    "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi"
                )
            )
        }
    }
    packagingOptions {
        jniLibs {
            excludes.add("META-INF/{AL2.0,LGPL2.1}")
        }
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/{AL2.0,LGPL2.1}",
                    "META-INF/CHANGES",
                    "META-INF/*.properties",
                    "META-INF/*.version",
                    "META-INF/*.md",
                    "DebugProbesKt.bin",
                    "kotlin-tooling-metadata.json"
                )
            )
        }
    }
}

dependencies {
    testImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.testExt)
    androidTestImplementation(Dependencies.Test.espresso)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.Material.material)
    implementation(Dependencies.Compose.activity)
    implementation(Dependencies.Compose.viewModel)
    implementation(Dependencies.Compose.constraintLayout)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.materialIcons)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.uiTooling)
    androidTestImplementation(Dependencies.Compose.uiTestJunit4)
    implementation(Dependencies.Accompanist.systemUiController)
    implementation(Dependencies.Accompanist.navigation)
    implementation(Dependencies.Coil.coil)
    implementation(Dependencies.Coil.coilGif)
    implementation(Dependencies.Base.coroutines)
    implementation(project(":common"))
    implementation(project(":proxy"))
}