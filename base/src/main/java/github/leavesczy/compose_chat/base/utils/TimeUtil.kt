package github.leavesczy.compose_chat.base.utils

import github.leavesczy.compose_chat.base.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
internal object TimeUtil {

    private fun formatTime(milliseconds: Long, patternResId: Int): String {
        val pattern = StringResources.getString(resId = patternResId)
        return SimpleDateFormat(pattern, Locale.ENGLISH).format(Date(milliseconds))
    }

    fun formatTimeYMDHMS(milliseconds: Long): String {
        return formatTime(
            milliseconds = milliseconds,
            patternResId = R.string.time_pattern_year_month_day_hour_minute
        )
    }

    fun formatConversationTime(milliseconds: Long): String {
        val conversationCalendar = Calendar.getInstance().apply {
            timeInMillis = milliseconds
        }
        val msgYear = conversationCalendar.get(Calendar.YEAR)
        val msgDayOfYear = conversationCalendar.get(Calendar.DAY_OF_YEAR)
        val nowCalendar = Calendar.getInstance()
        val nowYear = nowCalendar.get(Calendar.YEAR)
        val nowDayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR)
        return when (nowYear) {
            msgYear if nowDayOfYear == msgDayOfYear -> {
                formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_hour_minute
                )
            }

            msgYear if (nowDayOfYear - msgDayOfYear == 1) -> {
                StringResources.getString(resId = R.string.time_yesterday)
            }

            msgYear if (nowDayOfYear - msgDayOfYear == 2) -> {
                StringResources.getString(resId = R.string.time_day_before_yesterday)
            }

            msgYear if (nowDayOfYear - msgDayOfYear < 7) -> {
                weekDayString(dayOfWeek = conversationCalendar.get(Calendar.DAY_OF_WEEK))
            }

            msgYear -> {
                formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_month_day
                )
            }

            else -> {
                formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_year_month_day
                )
            }
        }
    }

    fun formatMessageTime(milliseconds: Long): String {
        val messageCalendar = Calendar.getInstance().apply {
            timeInMillis = milliseconds
        }
        val msgYear = messageCalendar.get(Calendar.YEAR)
        val msgDayOfYear = messageCalendar.get(Calendar.DAY_OF_YEAR)
        val nowCalendar = Calendar.getInstance()
        val nowYear = nowCalendar.get(Calendar.YEAR)
        val nowDayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR)
        return when (nowYear) {
            msgYear if nowDayOfYear == msgDayOfYear -> {
                formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_hour_minute
                )
            }

            msgYear if (nowDayOfYear - msgDayOfYear == 1) -> {
                val timeStr = formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_hour_minute
                )
                StringResources.getString(resId = R.string.time_yesterday_with_time, timeStr)
            }

            msgYear if (nowDayOfYear - msgDayOfYear == 2) -> {
                val timeStr = formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_hour_minute
                )
                StringResources.getString(
                    resId = R.string.time_day_before_yesterday_with_time,
                    timeStr
                )
            }

            msgYear if (nowDayOfYear - msgDayOfYear < 7) -> {
                val weekDay = weekDayString(dayOfWeek = messageCalendar.get(Calendar.DAY_OF_WEEK))
                val timeStr = formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_hour_minute
                )
                StringResources.getString(resId = R.string.time_weekday_with_time, weekDay, timeStr)
            }

            msgYear -> {
                formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_month_day_hour_minute
                )
            }

            else -> {
                formatTime(
                    milliseconds = milliseconds,
                    patternResId = R.string.time_pattern_year_month_day_hour_minute
                )
            }
        }
    }

    private fun weekDayString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> StringResources.getString(resId = R.string.time_monday)
            Calendar.TUESDAY -> StringResources.getString(resId = R.string.time_tuesday)
            Calendar.WEDNESDAY -> StringResources.getString(resId = R.string.time_wednesday)
            Calendar.THURSDAY -> StringResources.getString(resId = R.string.time_thursday)
            Calendar.FRIDAY -> StringResources.getString(resId = R.string.time_friday)
            Calendar.SATURDAY -> StringResources.getString(resId = R.string.time_saturday)
            Calendar.SUNDAY -> StringResources.getString(resId = R.string.time_sunday)
            else -> ""
        }
    }

}