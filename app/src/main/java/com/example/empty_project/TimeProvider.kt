package com.example.empty_project

import java.util.Calendar

interface TimeProvider {
    fun getCurrentHour(): Int
}

class DefaultTimeProvider : TimeProvider {
    override fun getCurrentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}
