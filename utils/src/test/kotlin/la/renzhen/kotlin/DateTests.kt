package la.renzhen.kotlin

import la.renzhen.kotlin.utils.*
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 31/08/2018 7:45 PM
 */

class DateTests {

    @Test
    fun testDate() {
        show(Date().offset(1, TimeUnit.HOURS))
        show(Date().yesterday())
        show(Date().monday())
        show(Date().sunday())
        show(Date().weekDay(Calendar.TUESDAY))

        show(Date().monthFirstDay())
        show(Date().monthLastDay())
        println(Date().weekName())
        println(Date().offset(-1).petty())

        println(Date().isInDay(Date().offset(1, TimeUnit.HOURS)))

        show(Date().round(Calendar.MINUTE))

        println(Date().monthFirstDay().range(Date().monthLastDay()).joinToString { it.format() })
        println(Date().monthFirstDay().round().list(Date().monthLastDay().round()).joinToString { it.format() })

        println(Date().round().list(Date().round(Calendar.MINUTE), TimeUnit.HOURS).joinToString { it.format() })

        println(Date().weekDays().joinToString { it.day() })
    }

    fun show(date: Date) {
        println(date.format())
    }

    @Test
    fun testMonthDay() {
        println("20180426".date("yyyyMMdd").monthLastDay().petty())

        println(Date().isWeekEnd(Calendar.MONDAY))

        println(Date().isWeekFirst())
    }
}