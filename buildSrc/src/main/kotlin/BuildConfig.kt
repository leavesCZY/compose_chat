import org.gradle.api.JavaVersion

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object BuildConfig {
    const val applicationId = "github.leavesczy.compose_chat"
    const val minSdk = 21
    const val targetSdk = 33
    const val compileSdk = 33
    const val jvmTarget = "11"
    val sourceCompatibility = JavaVersion.VERSION_11
    val targetCompatibility = JavaVersion.VERSION_11
    const val buildToolsVersion = "33.0.2"
    const val keyAlias = "leavesCZY"
    const val keyPassword = "123456"
    const val storePassword = "123456"
}