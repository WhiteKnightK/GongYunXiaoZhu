package vip.chuansvip.gongyunxiaozhu.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


fun getAllWeeksSinceLastYear(date: LocalDate): List<Pair<LocalDate, LocalDate>> {
    val weeksSinceLastYear = mutableListOf<Pair<LocalDate, LocalDate>>()

    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) // Start from Monday
    val lastYearStart = startOfWeek.minusWeeks(52) // Go back 52 weeks (1 year)

    var currentDate = lastYearStart

    while (currentDate <= startOfWeek) {
        val endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)) // End on Sunday
        weeksSinceLastYear.add(Pair(currentDate, endOfWeek))
        currentDate = currentDate.plusWeeks(1)
    }

    return weeksSinceLastYear
}


fun parseDateRange(input: String): Pair<String, String>? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")

    val parts = input.split(" ~ ")
    if (parts.size != 2) {
        return null
    }

    val startDate = LocalDate.parse(parts[0], formatter)
    val endDate = LocalDate.parse(parts[1], formatter)

    val startTime = LocalTime.of(0, 0, 0)
    val endTime = LocalTime.of(23, 59, 59)

    val startDateTime = LocalDateTime.of(startDate, startTime)
    val endDateTime = LocalDateTime.of(endDate, endTime)

    var stringStartDateTime = startDateTime.toString()
    stringStartDateTime = stringStartDateTime.replace("T", " ").replace("00:00","00:00:00")

    var stringEndDateTime = endDateTime.toString()
    stringEndDateTime = stringEndDateTime.replace("T", " ")


    return Pair(stringStartDateTime, stringEndDateTime)
}



fun getYearMonthRange(currentDate: LocalDate): List<String> {
    val year = currentDate.year
    val currentMonth = currentDate.monthValue
    val months = mutableListOf<String>()

    for (month in currentMonth downTo 1) {
        val formattedMonth = String.format("%d-%d", year, month)
        months.add(formattedMonth)
    }

    if (currentMonth > 1) {
        for (month in 12 downTo currentMonth + 1) {
            val lastYearMonth = String.format("%d-%d", year - 1, month)
            months.add(lastYearMonth)
        }
    }

    return months
}
//写一个判断日期的方法，传入年月日，判断是否晚于今天
fun isAfterToday(year: Int, month: Int, day: Int): Boolean {
    val today = LocalDate.now()
    val date = LocalDate.of(year, month, day)
    return date.isAfter(today)
}

fun isBeforeToday(year: Int, month: Int, day: Int): Boolean {
    val today = LocalDate.now()
    val date = LocalDate.of(year, month, day)
    return date.isBefore(today)
}


fun main() {
   println(isBeforeToday(2023, 8, 22))
}






