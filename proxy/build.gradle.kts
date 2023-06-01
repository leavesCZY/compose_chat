android {
    namespace = "github.leavesczy.compose_chat.proxy"
}

dependencies {
    implementation(project(":base"))
    implementationTest()
    implementation(Dependencies.Components.coroutines)
    implementation(Dependencies.Components.imSdk)
}