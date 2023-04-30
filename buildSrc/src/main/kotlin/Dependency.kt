import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private fun DependencyHandlerScope.implementationExtend(library: Any) {
    this.add("implementation", library)
}

private fun DependencyHandlerScope.debugImplementationExtend(library: Any) {
    this.add("debugImplementation", library)
}

private fun DependencyHandlerScope.androidTestImplementationExtend(library: Any) {
    this.add("androidTestImplementation", library)
}

private fun DependencyHandlerScope.testImplementationExtend(library: Any) {
    this.add("testImplementation", library)
}

fun DependencyHandlerScope.implementationTest() {
    testImplementationExtend(Dependencies.Test.junit)
    androidTestImplementationExtend(Dependencies.Test.testExt)
    androidTestImplementationExtend(Dependencies.Test.espresso)
}

fun DependencyHandlerScope.implementationCompose() {
    implementationExtend(platform(Dependencies.Compose.bom))
    androidTestImplementationExtend(platform(Dependencies.Compose.bom))
    implementationExtend(Dependencies.Compose.ui)
    implementationExtend(Dependencies.Compose.uiToolingPreview)
    debugImplementationExtend(Dependencies.Compose.uiTooling)
    debugImplementationExtend(Dependencies.Compose.uiTestManifest)
    androidTestImplementationExtend(Dependencies.Compose.uiTestJunit4)
    implementationExtend(Dependencies.Compose.material)
    implementationExtend(Dependencies.Compose.material3)
    implementationExtend(Dependencies.Compose.materialIcons)
    implementationExtend(Dependencies.Compose.constraintLayout)
    implementationExtend(Dependencies.Compose.activity)
    implementationExtend(Dependencies.Compose.viewModel)
}

fun DependencyHandlerScope.implementationCoil() {
    implementationExtend(Dependencies.Components.coilGif)
    implementationExtend(Dependencies.Components.coilCompose)
}