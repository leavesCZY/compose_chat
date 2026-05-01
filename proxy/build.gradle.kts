plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "github.leavesczy.compose_chat.proxy"
}

dependencies {
    implementation(project(":base"))
    implementation(libs.kotlinx.coroutines)
    implementation(libs.tencent.imsdk)
}