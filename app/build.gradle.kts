android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":proxy"))
    implementationTest()
    implementationCompose()
    implementationCoil()
    implementation(Dependencies.Accompanist.uiController)
    implementation(Dependencies.Components.appcompat)
    implementation(Dependencies.Components.coroutines)
    implementation(Dependencies.Components.matisse)
}