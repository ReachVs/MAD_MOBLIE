package com.example.empty_project

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], packageName = "com.example.empty_project")
class MainActivityTest {

    @Test
    fun activity_initialState_isCorrect() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val textView = activity.findViewById<TextView>(R.id.hello_text)
                val nameInput = activity.findViewById<EditText>(R.id.name_input)
                
                // Should contain "Madape" by default
                assert(textView.text.contains("Madape"))
                assertEquals("", nameInput.text.toString())
            }
        }
    }

    @Test
    fun typingName_updatesGreeting() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val textView = activity.findViewById<TextView>(R.id.hello_text)
                val nameInput = activity.findViewById<EditText>(R.id.name_input)
                
                nameInput.setText("Robo")
                
                assert(textView.text.contains("Robo"))
            }
        }
    }

    @Test
    fun clickingToggle_changesGreetingText() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val textView = activity.findViewById<TextView>(R.id.hello_text)
                val button = activity.findViewById<Button>(R.id.change_text_button)
                
                val initialText = textView.text.toString()
                button.performClick()
                val toggledText = textView.text.toString()
                
                assert(initialText != toggledText)
                assert(toggledText.contains("Text Changed"))
            }
        }
    }

    @Test
    fun activity_recreation_preservesState() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val nameInput = activity.findViewById<EditText>(R.id.name_input)
                val button = activity.findViewById<Button>(R.id.change_text_button)
                
                nameInput.setText("Persistence")
                button.performClick() // Toggle to "Text Changed" state
            }
            
            // Recreate activity (simulates rotation or process death recovery)
            scenario.recreate()
            
            scenario.onActivity { activity ->
                val nameInput = activity.findViewById<EditText>(R.id.name_input)
                val textView = activity.findViewById<TextView>(R.id.hello_text)
                
                assertEquals("Persistence", nameInput.text.toString())
                assert(textView.text.contains("Persistence"))
                assert(textView.text.contains("Text Changed"))
            }
        }
    }
}
