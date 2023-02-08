import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * @Author: leavesCZY
 * @Date: 2022/6/17 22:38
 * @Desc:
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
    implementationExtend(platform(Dependencies.Compose.composeBom))
    androidTestImplementationExtend(platform(Dependencies.Compose.composeBom))
    implementationExtend(Dependencies.Compose.materialIcons)
    implementationExtend(Dependencies.Compose.material3)
    implementationExtend(Dependencies.Compose.ui)
    debugImplementationExtend(Dependencies.Compose.uiTooling)
    debugImplementationExtend(Dependencies.Compose.uiTestJunit4)
    implementationExtend(Dependencies.Compose.constraintLayout)
    implementationExtend(Dependencies.Compose.activity)
    implementationExtend(Dependencies.Compose.viewModel)
}

fun DependencyHandlerScope.implementationCoil() {
    implementationExtend(Dependencies.Components.coil)
    implementationExtend(Dependencies.Components.coilGif)
}