/**
 * @Author: CZY
 * @Date: 2022/5/27 14:24
 * @Desc:
 */
object Dependencies {

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testExt = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }

    object AppCompat {
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val material = "com.google.android.material:material:1.8.0"
    }

    object Compose {
        const val composeCompilerVersion = "1.4.2"
        const val composeBom = "androidx.compose:compose-bom:2023.01.00"
        const val materialIcons = "androidx.compose.material:material-icons-extended"
        const val ui = "androidx.compose.ui:ui"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val material3 = "androidx.compose.material3:material3"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val activity = "androidx.activity:activity-compose:1.6.1"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
    }

    object Accompanist {
        private const val version = "0.28.0"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
    }

    object Components {
        private const val coilVersion = "2.2.2"
        const val coil = "io.coil-kt:coil-compose:$coilVersion"
        const val coilGif = "io.coil-kt:coil-gif:$coilVersion"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        const val matisse = "com.github.leavesCZY:Matisse:0.0.10"
        const val imSdk = "com.tencent.imsdk:imsdk-plus:7.0.3768"
    }

}