/**
 * @Author: CZY
 * @Date: 2022/5/27 14:24
 * @Desc:
 */
object Dependencies {

    private const val appCompatVersion = "1.6.0-alpha04"

    private const val lifecycleVersion = "2.5.0-rc01"

    const val composeVersion = "1.2.0-beta03"

    object Plugin {
        const val android = "com.android.tools.build:gradle:7.2.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testExt = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:$appCompatVersion"
    }

    object Material {
        const val material = "com.google.android.material:material:1.7.0-alpha02"
    }

    object Compose {
        const val activity = "androidx.activity:activity-compose:$appCompatVersion"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion"
        const val material = "androidx.compose.material:material:$composeVersion"
        const val materialIcons =
            "androidx.compose.material:material-icons-extended:$composeVersion"
        const val ui = "androidx.compose.ui:ui:$composeVersion"
        const val uiTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:$composeVersion"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha02"
        const val material3 = "androidx.compose.material3:material3:1.0.0-alpha13"
    }

    object Accompanist {
        private const val version = "0.24.10-beta"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val navigation =
            "com.google.accompanist:accompanist-navigation-animation:$version"
    }

    object Coil {
        private const val version = "2.1.0"
        const val coil = "io.coil-kt:coil-compose:$version"
        const val coilGif = "io.coil-kt:coil-gif:$version"
    }

    object Components {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2"
        const val matisse = "com.github.leavesCZY:Matisse:0.0.1"
        const val imSdk = "com.tencent.imsdk:imsdk-plus:6.2.2363"
    }

}