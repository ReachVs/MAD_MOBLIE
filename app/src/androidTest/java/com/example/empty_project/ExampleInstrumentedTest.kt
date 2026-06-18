package com.example.empty_project

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.empty_project", appContext.packageName)
    }

    @Test
    fun fullUserFlow_nameInput_toggle_reset() {
        // 1. Enter name
        onView(withId(R.id.name_input))
            .perform(typeText("Ceaser"), closeSoftKeyboard())

        // 2. Verify greeting contains name
        onView(withId(R.id.hello_text))
            .check(matches(withText(containsString("Ceaser"))))

        // 3. Toggle greeting
        onView(withId(R.id.change_text_button))
            .perform(click())

        // 4. Verify "Text Changed" is displayed with name
        onView(withId(R.id.hello_text))
            .check(matches(withText(containsString("Text Changed"))))
            .check(matches(withText(containsString("Ceaser"))))

        // 5. Reset
        onView(withId(R.id.reset_button))
            .perform(click())

        // 6. Verify reset to default (name cleared, greeting reset)
        onView(withId(R.id.name_input))
            .check(matches(withText("")))
        onView(withId(R.id.hello_text))
            .check(matches(withText(containsString("User"))))
            .check(matches(not(withText(containsString("Ceaser")))))
    }

    @Test
    fun nameInput_tooLong_showsError() {
        val longName = "A".repeat(NameValidator.MAX_NAME_LENGTH + 1)
        
        onView(withId(R.id.name_input))
            .perform(typeText(longName), closeSoftKeyboard())

        // Verify error message is shown (Checking for error text in the layout)
        onView(withId(R.id.name_input))
            .check(matches(hasErrorText("Name is too long!")))
    }
}
