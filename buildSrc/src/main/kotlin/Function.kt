import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: CZY
 * @Date: 2022/5/27 14:24
 * @Desc:
 */
object Function {

    fun getFormattedTime(): String {
        val date = Date()
        val format = "yyyy_MM_dd_HH_mm_ss"
        return SimpleDateFormat(format, Locale.CHINA).format(date)
    }

}