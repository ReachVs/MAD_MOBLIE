package com.example.empty_project

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun ui_basicInteractions_workCorrectly() {
        // Initial state
        onView(withId(R.id.hello_text)).check(matches(withText(containsString("Madape"))))

        // Type name
        onView(withId(R.id.name_input)).perform(typeText("Android"), closeSoftKeyboard())
        onView(withId(R.id.hello_text)).check(matches(withText(containsString("Android"))))

        // Toggle greeting
        val initialText = getText(withId(R.id.hello_text))
        onView(withId(R.id.change_text_button)).perform(click())
        onView(withId(R.id.hello_text)).check(matches(not(withText(initialText))))
        onView(withId(R.id.hello_text)).check(matches(withText(containsString("Text Changed"))))

        // Reset
        onView(withId(R.id.reset_button)).perform(click())
        onView(withId(R.id.hello_text)).check(matches(withText(containsString("Madape"))))
        onView(withId(R.id.name_input)).check(matches(withText("")))
    }

    @Test
    fun ui_errorShowing_whenNameTooLong() {
        val longName = "A".repeat(21)
        onView(withId(R.id.name_input)).perform(typeText(longName), closeSoftKeyboard())
        
        onView(withId(R.id.name_input)).check(matches(hasErrorText("Name is too long!")))
    }

    // Helper to get text from a view matcher
    private fun getText(matcher: org.hamcrest.Matcher<android.view.View>): String {
        var text = ""
        onView(matcher).perform(object : androidx.test.espresso.ViewAction {
            override fun getConstraints(): org.hamcrest.Matcher<android.view.View> = isAssignableFrom(android.widget.TextView::class.java)
            override fun getDescription(): String = "getting text from a TextView"
            override fun perform(uiController: androidx.test.espresso.UiController, view: android.view.View) {
                val tv = view as android.widget.TextView
                text = tv.text.toString()
            }
        })
        return text
    }
}
