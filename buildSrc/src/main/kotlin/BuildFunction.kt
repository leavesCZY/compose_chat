import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object BuildFunction {

    fun getFormattedTime(): String {
        val format = "yyyy_MM_dd_HH_mm_ss"
        return SimpleDateFormat(format, Locale.CHINA).format(Date())
    }

}