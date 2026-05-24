package github.leavesczy.compose_chat

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
internal fun Project.configureAndroidApplication(applicationExtension: ApplicationExtension) {
    val buildTimeProvider = providers.provider {
        formattedTime(pattern = "yyyy-MM-dd HH:mm:ss")
    }
    val apkTimeProvider = providers.provider {
        formattedTime(pattern = "yyyyMMdd_HHmmss")
    }
    applicationExtension.apply {
        defaultConfig {
            applicationId = "github.leavesczy.compose_chat"
            targetSdk {
                version = release(version = androidTargetSdkVersion())
            }
            versionCode = appVersionCode()
            versionName = appVersionName()
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
            buildConfigField("String", "BUILD_TIME", "\"${buildTimeProvider.get()}\"")
        }
        val basePluginExtension = project.extensions.getByType<BasePluginExtension>()
        basePluginExtension.apply {
            archivesName.set("compose_chat_v${defaultConfig.versionName}_${defaultConfig.versionCode}_${apkTimeProvider.get()}")
        }
        androidResources {
            localeFilters.clear()
            localeFilters.add(element = "zh")
        }
        signingConfigs {
            create("release") {
                storeFile = File(File(rootDir, "doc"), "key.jks")
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
            val releaseSigning = signingConfigs.findByName("release")
            debug {
                signingConfig = releaseSigning
                isMinifyEnabled = false
                isShrinkResources = false
                isDebuggable = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
            release {
                signingConfig = releaseSigning
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
                excludes.add(element = "META-INF/{AL2.0,LGPL2.1}")
            }
            resources {
                excludes.addAll(
                    elements = listOf(
                        "**/*.md",
                        "**/*.version",
                        "**/*.properties",
                        "**/*.kotlin_module",
                        "**/CHANGES",
                        "**/LICENSE.txt",
                        "**/{AL2.0,LGPL2.1}",
                        "**/DebugProbesKt.bin",
                        "**/app-metadata.properties",
                        "**/kotlin-tooling-metadata.json",
                        "**/version-control-info.textproto",
                        "**/androidsupportmultidexversion.txt"
                    )
                )
            }
        }
    }
}

private fun formattedTime(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).format(formatter)
}