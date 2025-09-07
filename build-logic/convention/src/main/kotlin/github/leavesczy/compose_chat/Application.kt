package github.leavesczy.compose_chat

import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun Project.configureAndroidApplication() {
    configure<BaseAppModuleExtension> {
        defaultConfig {
            applicationId = "github.leavesczy.compose_chat"
            targetSdk = 36
            versionCode = 1
            versionName = "1.0.0"
            applicationVariants.all {
                val variant = this
                outputs.all {
                    if (this is ApkVariantOutputImpl) {
                        this.outputFileName =
                            "compose_chat_${variant.name}_${getApkBuildTime()}.apk"
                    }
                }
            }
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
            buildConfigField("String", "BUILD_TIME", "\"${getBuildConfigTime()}\"")
        }
        androidResources {
            localeFilters += setOf("zh")
        }
        signingConfigs {
            create("release") {
                storeFile = File(rootDir.absolutePath, "key.jks")
                keyAlias = "leavesCZY"
                keyPassword = "123456"
                storePassword = "123456"
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
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
        buildFeatures {
            buildConfig = true
        }
        packaging {
            jniLibs {
                excludes += setOf("META-INF/{AL2.0,LGPL2.1}")
            }
            resources {
                excludes += setOf(
                    "**/*.md",
                    "**/*.version",
                    "**/*.properties",
                    "**/LICENSE.txt",
                    "**/DebugProbesKt.bin",
                    "**/app-metadata.properties",
                    "**/kotlin-tooling-metadata.json",
                    "**/version-control-info.textproto",
                    "**/androidsupportmultidexversion.txt",
                    "META-INF/CHANGES",
                    "META-INF/{AL2.0,LGPL2.1}",
                    "META-INF/**/*.kotlin_module",
                    "META-INF/version-control-info.textproto",
                )
            }
        }
    }
}

private fun getTime(pattern: String): String {
    val now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return now.format(formatter)
}

private fun getApkBuildTime(): String {
    return getTime(pattern = "yyyyMMdd_HHmmss")
}

private fun getBuildConfigTime(): String {
    return getTime(pattern = "yyyy-MM-dd HH:mm:ss")
}