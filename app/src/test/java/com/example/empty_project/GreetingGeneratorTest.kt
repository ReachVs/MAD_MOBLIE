package com.example.empty_project

import org.junit.Assert.assertEquals
import org.junit.Test

class GreetingGeneratorTest {

    private val generator = GreetingGenerator()

    @Test
    fun getTimeOfDay_exhaustiveCheck() {
        val expectedMap = mapOf(
            0 to GreetingGenerator.TimeOfDay.NIGHT,
            1 to GreetingGenerator.TimeOfDay.NIGHT,
            2 to GreetingGenerator.TimeOfDay.NIGHT,
            3 to GreetingGenerator.TimeOfDay.NIGHT,
            4 to GreetingGenerator.TimeOfDay.NIGHT,
            5 to GreetingGenerator.TimeOfDay.MORNING,
            6 to GreetingGenerator.TimeOfDay.MORNING,
            7 to GreetingGenerator.TimeOfDay.MORNING,
            8 to GreetingGenerator.TimeOfDay.MORNING,
            9 to GreetingGenerator.TimeOfDay.MORNING,
            10 to GreetingGenerator.TimeOfDay.MORNING,
            11 to GreetingGenerator.TimeOfDay.MORNING,
            12 to GreetingGenerator.TimeOfDay.AFTERNOON,
            13 to GreetingGenerator.TimeOfDay.AFTERNOON,
            14 to GreetingGenerator.TimeOfDay.AFTERNOON,
            15 to GreetingGenerator.TimeOfDay.AFTERNOON,
            16 to GreetingGenerator.TimeOfDay.AFTERNOON,
            17 to GreetingGenerator.TimeOfDay.EVENING,
            18 to GreetingGenerator.TimeOfDay.EVENING,
            19 to GreetingGenerator.TimeOfDay.EVENING,
            20 to GreetingGenerator.TimeOfDay.EVENING,
            21 to GreetingGenerator.TimeOfDay.NIGHT,
            22 to GreetingGenerator.TimeOfDay.NIGHT,
            23 to GreetingGenerator.TimeOfDay.NIGHT
        )

        for (hour in 0..23) {
            assertEquals("Failed at hour $hour", expectedMap[hour], generator.getTimeOfDay(hour))
        }
    }

    @Test
    fun getGreeting_returnsCorrectResource_whenNotChanged() {
        assertEquals(R.string.good_morning, generator.getGreeting(false, GreetingGenerator.TimeOfDay.MORNING))
        assertEquals(R.string.good_afternoon, generator.getGreeting(false, GreetingGenerator.TimeOfDay.AFTERNOON))
        assertEquals(R.string.good_evening, generator.getGreeting(false, GreetingGenerator.TimeOfDay.EVENING))
        assertEquals(R.string.good_night, generator.getGreeting(false, GreetingGenerator.TimeOfDay.NIGHT))
    }

    @Test
    fun getGreeting_returnsChanged_whenChangedRegardlessOfTime() {
        for (timeOfDay in GreetingGenerator.TimeOfDay.values()) {
            assertEquals(
                "Failed for $timeOfDay",
                R.string.greeting_changed,
                generator.getGreeting(true, timeOfDay)
            )
        }
    }
}
