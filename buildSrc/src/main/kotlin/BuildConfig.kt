import org.gradle.api.JavaVersion

/**
 * @Author: CZY
 * @Date: 2022/5/27 14:24
 * @Desc:
 */
object BuildConfig {
    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = JavaVersion.VERSION_1_8
    const val jvmTarget = "1.8"
    const val applicationId = "github.leavesczy.compose_chat"
    const val compileSdk = 33
    const val buildToolsVersion = "33.0.0"
    const val minSdk = 21
    const val targetSdk = 32
    const val versionCode = 23
    const val versionName = "1.6.3"
    const val keyAlias = "leavesCZY"
    const val keyPassword = "123456"
    const val storePassword = "123456"
}