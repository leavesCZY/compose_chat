plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.android.compose)
    alias(libs.plugins.app.kotlin.parcelize)
    alias(libs.plugins.app.leavesczy.track)
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
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.leavesczy.matisse)
    implementation(libs.coil.gif)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.squareup.okHttp)
}