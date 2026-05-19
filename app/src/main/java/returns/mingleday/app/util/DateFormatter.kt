package returns.mingleday.app.util

import java.time.LocalDate
import java.time.LocalDateTime

object DateFormatter {
    fun LocalDateTime.formatCustom(type: Int = 1): String {
        val now = LocalDateTime.now()

        val isToday = this.toLocalDate() == now.toLocalDate()
        val isThisYear = this.year == now.year

        val year = this.year
        val month = "%02d".format(this.monthValue)
        val day = "%02d".format(this.dayOfMonth)
        val hour = "%02d".format(this.hour)
        val minute = "%02d".format(this.minute)
        val second = "%02d".format(this.second)

        return when (type) {
            1 -> "${year}년 ${month}월 ${day}일"
            2 -> "$year.$month.$day"
            3 -> when {
                isToday -> "$hour:$minute"
                isThisYear -> "$month.$day"
                else -> "$year.$month.$day"
            }
            4 -> "${year}년 ${month}월 ${day}일 $hour:$minute"
            6 -> "$hour:$minute:$second"
            7 -> "${year}년 ${month}월 ${day}일 $hour:$minute:$second"
            8 -> "$hour:$minute"
            9 -> when {
                isToday -> "$hour:$minute:$second"
                isThisYear -> "$month.$day"
                else -> "$year.$month.$day"
            }
            else -> this.toString()
        }
    }

    fun LocalDate.formatCustom(type: Int = 1): String {
        val now = LocalDate.now()

        val isToday = this == now
        val isThisYear = this.year == now.year

        val year = this.year
        val month = "%02d".format(this.monthValue)
        val day = "%02d".format(this.dayOfMonth)

        return when (type) {
            1 -> "${year}년 ${month}월 ${day}일"
            2 -> "$year.$month.$day"
            3 -> when {
                isToday -> "오늘"
                isThisYear -> "$month.$day"
                else -> "$year.$month.$day"
            }
            else -> this.toString()
        }
    }
}