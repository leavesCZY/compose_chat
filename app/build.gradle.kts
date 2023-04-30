android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementationTest()
    implementationCompose()
    implementationCoil()
    implementation(Dependencies.Appcompat.appcompat)
    implementation(Dependencies.Appcompat.material)
    implementation(Dependencies.Accompanist.uiController)
    implementation(Dependencies.Components.coroutines)
    implementation(Dependencies.Components.matisse)
    implementation(project(":base"))
    implementation(project(":proxy"))
}