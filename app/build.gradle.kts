plugins {
    id("com.android.application")
    kotlin("android")
}

apply {
    plugin<ManagerPlugin>()
}

dependencies {
    testImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.testExt)
    androidTestImplementation(Dependencies.Test.espresso)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.Material.material)
    implementation(Dependencies.Compose.activity)
    implementation(Dependencies.Compose.viewModel)
    implementation(Dependencies.Compose.constraintLayout)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.materialIcons)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.uiTooling)
    androidTestImplementation(Dependencies.Compose.uiTestJunit4)
    implementation(Dependencies.Accompanist.systemUiController)
    implementation(Dependencies.Accompanist.navigation)
    implementation(Dependencies.Coil.coil)
    implementation(Dependencies.Coil.coilGif)
    implementation(Dependencies.Base.coroutines)
    implementation(Dependencies.Base.matisse)
    implementation(project(":common"))
    implementation(project(":proxy"))
}