package com.example.empty_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.activity.viewModels
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val textView = findViewById<TextView>(R.id.hello_text)
        val button = findViewById<Button>(R.id.change_text_button)
        val reset_button = findViewById<Button>(R.id.reset_button)
        val nameInput = findViewById<EditText>(R.id.name_input)

        viewModel.greetingResource.observe(this) { greetingRes ->
            updateText(textView, greetingRes, viewModel.name.value ?: "")
        }

        viewModel.name.observe(this) { name ->
            if (nameInput.text.toString() != name) {
                nameInput.setText(name)
            }
            viewModel.greetingResource.value?.let { res ->
                updateText(textView, res, name)
            }
        }

        viewModel.nameError.observe(this) { hasError ->
            if (hasError) {
                nameInput.error = getString(R.string.name_too_long)
            } else {
                nameInput.error = null
            }
        }

        nameInput.doAfterTextChanged { text ->
            viewModel.setName(text?.toString() ?: "")
        }

        button.setOnClickListener {
            viewModel.toggleGreeting()
        }

        reset_button.setOnClickListener {
            viewModel.reset()
        }

        // Initial update
        if (viewModel.greetingResource.value == null) {
            viewModel.updateGreeting()
        }
    }

    private fun updateText(textView: TextView, resourceId: Int, name: String) {
        val displayName = if (name.isBlank()) getString(R.string.default_name) else name
        textView.text = getString(resourceId, displayName)
    }
}
