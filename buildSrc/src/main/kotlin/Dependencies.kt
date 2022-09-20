/**
 * @Author: CZY
 * @Date: 2022/5/27 14:24
 * @Desc:
 */
object Dependencies {

    object Plugin {
        const val android = "com.android.tools.build:gradle:7.3.0"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testExt = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }

    object AppCompat {
        const val appcompat = "androidx.appcompat:appcompat:1.6.0-rc01"
        const val activity = "androidx.activity:activity-compose:1.6.0-rc02"
        const val material = "com.google.android.material:material:1.8.0-alpha01"
    }

    object Compose {
        private const val composeVersion = "1.3.0-beta02"
        const val composeCompilerVersion = "1.3.1"
        const val materialIcons =
            "androidx.compose.material:material-icons-extended:$composeVersion"
        const val ui = "androidx.compose.ui:ui:$composeVersion"
        const val uiTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:$composeVersion"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0-alpha02"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha03"
        const val material3 = "androidx.compose.material3:material3:1.0.0-beta02"
    }

    object Accompanist {
        private const val version = "0.26.3-beta"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
    }

    object Components {
        private const val coilVersion = "2.2.1"
        const val coil = "io.coil-kt:coil-compose:$coilVersion"
        const val coilGif = "io.coil-kt:coil-gif:$coilVersion"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        const val matisse = "com.github.leavesCZY:Matisse:0.0.6"
        const val imSdk = "com.tencent.imsdk:imsdk-plus:6.6.3002"
    }

}