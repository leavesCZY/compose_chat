plugins {
    alias(libs.plugins.chat.android.application)
    alias(libs.plugins.chat.android.compose)
    alias(libs.plugins.chat.kotlin.parcelize)
    alias(libs.plugins.chat.leavesczy.trace)
}

android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":proxy"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.exifinterface)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.leavesczy.matisse)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)
    implementation(libs.zoom.image.coil)
}