package la.renzhen.kotlin.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

internal val FORMAT_DAY = "yyyy-MM-dd"
internal val FORMATE_TIME = "HH:mm:ss"
internal val DEFAULT_FORMATE = "yyyy-MM-dd HH:mm:ss"


fun Date.offset(offset: Int, field: TimeUnit = TimeUnit.DAYS): Date {
    return Date(this.time + field.toMillis(offset.toLong()))
}

fun Date.yesterday(): Date {
    return this.offset(offset = -1)
}

/**
 * 获取所在所在星期的周day日期
 * @param day 星期
 * @see Calendar.MONDAY,Calendar.SATURDAY
 */
fun Date.weekDay(day: Int): Date {
    val cal = Calendar.getInstance(Locale.CHINA)
    cal.time = this
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, day)
    return cal.time
}

/**
 * @return 获取周一
 */
fun Date.monday(): Date {
    return weekDay(Calendar.MONDAY)
}

/**
 * @return 获取星期天
 */
fun Date.sunday(): Date {
    return weekDay(Calendar.SUNDAY)
}

/**
 * 格式化日期
 * @param pattern 格式化方式
 */
fun Date.format(pattern: String = DEFAULT_FORMATE): String {
    return SimpleDateFormat(pattern).format(this)
}

fun Date.day(): String {
    return this.format(FORMAT_DAY)
}

fun Date.monthFirstDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.monthLastDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DAY_OF_MONTH, 0)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.weekName(): String {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val weekCode = calendar.get(Calendar.DAY_OF_WEEK)
    when (weekCode) {
        Calendar.MONDAY -> return "周一"
        Calendar.TUESDAY -> return "周二"
        Calendar.WEDNESDAY -> return "周三"
        Calendar.THURSDAY -> return "周四"
        Calendar.FRIDAY -> return "周五"
        Calendar.SATURDAY -> return "周六"
        Calendar.SUNDAY -> return "周日"
    }
    return ""
}


/**
 * 优雅的方式显示日期
 *
 * @param date 需要显示的日期
 * @return 显示内容
 */
fun Date.petty(): String {
    val calendar = Calendar.getInstance()
    val delta = calendar.timeInMillis - this.time
    if (delta < 0) {
        return this.format()
    }

    if (delta < TimeUnit.MINUTES.toMillis(1)) {
        return TimeUnit.MILLISECONDS.toSeconds(delta).toString() + "秒前"
    } else if (delta < TimeUnit.HOURS.toMillis(1)) {
        return TimeUnit.MILLISECONDS.toMinutes(delta).toString() + "分钟前"
    } else if (delta < TimeUnit.DAYS.toMillis(1)) {
        return TimeUnit.MILLISECONDS.toHours(delta).toString() + "小时前"
    } else {
        val day = TimeUnit.MILLISECONDS.toDays(delta).toInt()
        when (day) {
            1 -> return "昨天 " + this.format(FORMATE_TIME)
            2 -> return "前天 " + this.format(FORMATE_TIME)
            else -> if (day < 7) {
                return if (this.isInWeek(calendar.time)) {
                    "本" + this.weekName() + this.format(FORMATE_TIME)
                } else {
                    "上" + this.weekName() + this.format(FORMATE_TIME)
                }
            }
        }
    }
    return this.format()
}

fun Date.isInDay(date: Date = Date()): Boolean {
    return this.round().equals(date.round())
}

fun Date.isInWeek(date: Date = Date()): Boolean {
    return this.sunday().isInDay(date.sunday())
}

fun Date.isInMonth(date: Date = Date()): Boolean {
    return this.monthFirstDay().isInDay(date.monthFirstDay())
}

fun Date.round(field: Int = Calendar.HOUR): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    when (field) {
        Calendar.HOUR -> {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
        Calendar.MINUTE -> {
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
        Calendar.SECOND -> {
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
    }
    return calendar.time
}

/**
 * 列举当前日期到结束时间的日期,包括开始时间和结束时间
 */
fun Date.list(end: Date, unit: TimeUnit = TimeUnit.DAYS): List<Date> {
    val dates = ArrayList<Date>()
    dates.add(this)
    dates.addAll(this.range(end, unit))
    dates.add(end);
    return dates
}

/**
 * 列举当前时间到结束时间，之前的日期,不包括开始结束时间
 */
fun Date.range(end: Date, unit: TimeUnit = TimeUnit.DAYS): List<Date> {
    var dates = ArrayList<Date>()
    var start = this
    while (start.before(end)) {
        start = start.offset(1, unit)
        if (start.before(end)) {
            dates.add(start)
        } else {
            break;
        }
    }
    return dates;
}

/**
 * 列举当前日期所有在所有星期日期
 */
fun Date.weekDays(): List<Date> {
    return this.monday().round().list(this.sunday().round())
}

