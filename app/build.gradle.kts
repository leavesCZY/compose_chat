android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementationTest()
    implementationCompose()
    implementationCoil()
    implementation(Dependencies.AppCompat.appcompat)
    implementation(Dependencies.AppCompat.material)
    implementation(Dependencies.Accompanist.systemUiController)
    implementation(Dependencies.Components.coroutines)
    implementation(Dependencies.Components.matisse)
    implementation(project(":common"))
    implementation(project(":proxy"))
}