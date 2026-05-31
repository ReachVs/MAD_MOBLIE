package com.example.mad_final

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mad_final.ui.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Use a simple FrameLayout to host the fragment
        val container = android.widget.FrameLayout(this).apply {
            id = android.view.View.generateViewId()
        }
        setContentView(container)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(container.id, MainFragment())
                .commit()
        }
    }
}
