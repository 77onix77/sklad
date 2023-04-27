package com.onix77.sklad

import kotlinx.datetime.Clock
import java.text.SimpleDateFormat
import java.util.*

class MyDate {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun getDate():String = dateFormat.format(Clock.System.now().toEpochMilliseconds())

    fun getTime():String = timeFormat.format(Clock.System.now().toEpochMilliseconds())

    fun getBeginMount(): String {
        val date = dateFormat.format(Clock.System.now().toEpochMilliseconds())
        return date.substring(0, 8) + "01"
    }

    fun getDateTimeForName() = "${getDate()}${getTime()}".replace("-", "").replace(":", "")




}