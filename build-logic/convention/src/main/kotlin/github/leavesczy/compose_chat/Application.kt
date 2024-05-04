@file:Suppress("UnstableApiUsage")

package github.leavesczy.compose_chat

import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun Project.configureAndroidApplication(commonExtension: BaseAppModuleExtension) {
    commonExtension.apply {
        defaultConfig {
            applicationId = "github.leavesczy.compose_chat"
            targetSdk = 34
            versionCode = 1
            versionName = "1.0.0"
            resourceConfigurations.add("zh")
            applicationVariants.all {
                val variant = this
                outputs.all {
                    if (this is ApkVariantOutputImpl) {
                        this.outputFileName =
                            "compose_chat_${variant.name}_${variant.versionCode}_${variant.versionName}_${getApkBuildTime()}.apk"
                    }
                }
            }
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
            buildConfigField("String", "BUILD_TIME", "\"${getBuildConfigTime()}\"")
        }
        signingConfigs {
            create("release") {
                storeFile =
                    File(rootDir.absolutePath + File.separator + "doc" + File.separator + "key.jks")
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
                    "**/**/*.properties",
                    "META-INF/{AL2.0,LGPL2.1}",
                    "META-INF/CHANGES",
                    "DebugProbesKt.bin",
                    "kotlin-tooling-metadata.json"
                )
            }
        }
    }
}

private fun getTime(pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    val time = Calendar.getInstance().time
    return simpleDateFormat.format(time)
}

private fun getApkBuildTime(): String {
    return getTime(pattern = "yyyy_MM_dd_HH_mm_ss")
}

private fun getBuildConfigTime(): String {
    return getTime(pattern = "yyyy-MM-dd HH:mm:ss")
}