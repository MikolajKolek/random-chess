package pl.edu.uj.tcs.rchess.model

import java.time.LocalDateTime

fun pgnTagStringToTags(tags: String): Map<String, String> {
    val regex = Regex("\\[\\s*([a-zA-Z0-9_]+)\\s*\"([^\"]+)\"]\n")

    val map = mutableMapOf<String, String>()
    for(match in regex.findAll(tags))
        map[match.groupValues[1]] = match.groupValues[2]

    return map
}

fun pgnDateToLocalDateTime(date: String): LocalDateTime {
    val pgnDateRegex = Regex("([\\d?]{4})\\.([\\d?]{2})\\.([\\d?]{2})")
    var (year, month, day) = pgnDateRegex.find(date)!!.destructured

    if(year.contains('?'))
        year = LocalDateTime.now().year.toString()
    if(month.contains('?')) {
        month = if(year == LocalDateTime.now().year.toString())
            LocalDateTime.now().monthValue.toString()
        else
            "01"
    }
    if(day.contains('?')) {
        day = if(year == LocalDateTime.now().year.toString() && month == LocalDateTime.now().monthValue.toString())
            LocalDateTime.now().dayOfMonth.toString()
        else
            "01"
    }

    return LocalDateTime.of(year.toInt(), month.toInt(), day.toInt(), 0, 0)
}