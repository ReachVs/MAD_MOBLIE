package com.example.empty_project

import java.util.Calendar

class GreetingGenerator {
    
    enum class TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }

    fun getGreeting(isChanged: Boolean, timeOfDay: TimeOfDay = TimeOfDay.MORNING): Int {
        if (isChanged) return R.string.greeting_changed
        
        return when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.good_morning
            TimeOfDay.AFTERNOON -> R.string.good_afternoon
            TimeOfDay.EVENING -> R.string.good_evening
            TimeOfDay.NIGHT -> R.string.good_night
        }
    }

    fun getTimeOfDay(hour: Int): TimeOfDay {
        return when (hour) {
            in 5..11 -> TimeOfDay.MORNING
            in 12..16 -> TimeOfDay.AFTERNOON
            in 17..20 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
    }
}
