plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.android.compose)
    alias(libs.plugins.app.kotlin.parcelize)
}

android {
    namespace = "github.leavesczy.compose_chat.base"
}

dependencies {
    implementation(libs.kotlinx.coroutines)
}