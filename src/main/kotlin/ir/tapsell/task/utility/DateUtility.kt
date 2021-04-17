package ir.tapsell.task.utility

import net.time4j.PlainDate
import net.time4j.calendar.PersianCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class DateUtility {
    companion object {

        /**
         * Used to create a random date starting from 2016-3-20 (1395/01/01)
         * @return random Date
         */
        fun generateRandomDate(): Date {
            val startDate = LocalDate.of(2016, 3, 20)
            return between(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date()
            )
        }

        private fun between(startInclusive: Date, endExclusive: Date): Date {
            val startMillis: Long = startInclusive.time
            val endMillis: Long = endExclusive.time
            val randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis)
            return Date(randomMillisSinceEpoch)
        }

        /**
         * Used to convert Gregorian date to solar
         * @param date This is a Gregorian date and is of the java.util.Date class type
         * @return PersianCalendar
         */
        fun convertGregorianToSolar(date: Date): PersianCalendar {
            val c = Calendar.getInstance()
            c.timeInMillis = date.time
            return PlainDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
                .transform(PersianCalendar::class.java)
        }
    }
}