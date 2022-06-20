dependencies {
    implementationTest()
    implementationCompose()
    implementationCoil()
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.Material.material)
    implementation(Dependencies.Accompanist.systemUiController)
    implementation(Dependencies.Accompanist.navigation)
    implementation(Dependencies.Components.coroutines)
    implementation(Dependencies.Components.matisse)
    implementation(project(":common"))
    implementation(project(":proxy"))
}