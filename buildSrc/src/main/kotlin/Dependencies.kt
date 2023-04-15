/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object Dependencies {

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testExt = "androidx.test.ext:junit:1.1.5"
        const val espresso = "androidx.test.espresso:espresso-core:3.5.1"
    }

    object AppCompat {
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val material = "com.google.android.material:material:1.8.0"
    }

    object Compose {
        const val composeCompilerVersion = "1.4.4"
        const val composeBom = "androidx.compose:compose-bom:2023.04.00"
        const val ui = "androidx.compose.ui:ui"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
        const val material = "androidx.compose.material:material"
        const val material3 = "androidx.compose.material3:material3"
        const val materialIcons = "androidx.compose.material:material-icons-extended"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val activity = "androidx.activity:activity-compose:1.7.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
    }

    object Accompanist {
        private const val version = "0.30.1"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
    }

    object Components {
        private const val coilVersion = "2.3.0"
        const val coilGif = "io.coil-kt:coil-gif:$coilVersion"
        const val coilCompose = "io.coil-kt:coil-compose:$coilVersion"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        const val matisse = "com.github.leavesCZY:Matisse:fed47eeadb"
        const val imSdk = "com.tencent.imsdk:imsdk-plus:7.1.3933"
    }

}