package com.minhto28.dev.chat_app.ui.main

import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.utils.showMessage

class MainActivity : AppCompatActivity() {
    private var EXIT = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        showMessage("App exit confirmation", this) {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
        }
        return super.getOnBackInvokedDispatcher()
    }
}