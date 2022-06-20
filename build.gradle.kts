buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(Dependencies.Plugin.android)
        classpath(Dependencies.Plugin.kotlin)
    }
}

subprojects {
    apply {
        plugin<ManagerPlugin>()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}