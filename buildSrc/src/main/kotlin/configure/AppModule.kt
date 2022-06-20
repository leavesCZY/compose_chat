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
internal fun BaseAppModuleExtension.appModule(project: Project) {
    compileSdk = BuildConfig.compileSdk
    buildToolsVersion = BuildConfig.buildToolsVersion
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
        applicationVariants.all {
            val variant = this
            outputs.all {
                if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                    this.outputFileName =
                        "compose_chat_${variant.name}_versionCode_${variant.versionCode}_versionName_${variant.versionName}_${BuildFunction.getFormattedTime()}.apk"
                }
            }
        }
        buildConfigField("String", "VERSION_NAME", "\"${BuildConfig.versionName}\"")
    }
    signingConfigs {
        create("release") {
            storeFile = File(project.rootDir.absolutePath + File.separator + "key.jks")
            keyAlias = BuildConfig.keyAlias
            storePassword = BuildConfig.storePassword
            keyPassword = BuildConfig.keyPassword
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
                    "DebugProbesKt.bin",
                    "kotlin-tooling-metadata.json",
                    "**/*.md",
                    "**/*.properties",
                    "**/**/*.properties",
                    "**/*.version"
                )
            )
        }
    }
}