package com.example.myapplication.utils.date_formatters

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    val russianLocale = Locale("ru", "RU")

    private val dateFormatDifficult = SimpleDateFormat("dd.MM.yyyy HH:mm", russianLocale).apply { timeZone = TimeZone.getTimeZone("UTC") }

    fun timestampToDateStr(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        return dateFormatDifficult.format(date)
    }
}