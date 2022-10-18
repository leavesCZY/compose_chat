android {
    namespace = "github.leavesczy.compose_chat.proxy"
}

dependencies {
    implementationTest()
    implementation(Dependencies.Components.coroutines)
    implementation(Dependencies.Components.imSdk)
    implementation(project(":common"))
}