import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.io.File

/**
 * @Author: CZY
 * @Date: 2022/5/29 17:20
 * @Desc:
 */
internal fun BaseAppModuleExtension.app(project: Project) {
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
            storeFile = File(project.rootDir.absolutePath + File.separator + "key.jks")
            keyAlias = BuildConfig.keyAlias
            storePassword = BuildConfig.keyPassword
            keyPassword = BuildConfig.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    buildTypes {
        debug {
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
        release {
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
    ((this as ExtensionAware).extensions.getByName("kotlinOptions") as KotlinJvmOptions).apply {
        jvmTarget = BuildConfig.jvmTarget
        freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
            addAll(
                listOf(
                    "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
                    "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
                    "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
//                    "-Xjvm-default=all",
//                    "-Xallow-jvm-ir-dependencies",
//                    "-Xskip-prerelease-check",
//                    "-Xopt-in=kotlin.RequiresOptIn",
//                    "-Xopt-in=com.google.accompanist.insets.ExperimentalAnimatedInsets",
//                    "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi",
//                    "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
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