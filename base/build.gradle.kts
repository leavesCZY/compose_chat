plugins {
    alias(libs.plugins.chat.android.library)
    alias(libs.plugins.chat.android.compose)
    alias(libs.plugins.chat.kotlin.parcelize)
}

android {
    namespace = "github.leavesczy.compose_chat.base"
}

dependencies {
    implementation(libs.kotlinx.coroutines)
}