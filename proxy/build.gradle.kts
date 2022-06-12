plugins {
    id("com.android.library")
    kotlin("android")
}

apply {
    plugin<ManagerPlugin>()
}

dependencies {
    testImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.testExt)
    androidTestImplementation(Dependencies.Test.espresso)
    implementation(Dependencies.Base.coroutines)
    implementation(Dependencies.Base.imSdk)
    implementation(project(":common"))
}