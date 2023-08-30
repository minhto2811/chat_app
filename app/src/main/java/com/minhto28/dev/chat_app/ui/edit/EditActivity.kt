package com.minhto28.dev.chat_app.ui.edit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.ActivityEditBinding
import com.minhto28.dev.chat_app.ui.edit.information.InformationFragment
import com.minhto28.dev.chat_app.ui.edit.password.PasswordFragment
import com.minhto28.dev.chat_app.utils.showMessage

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        when (intent.extras?.getInt("idEdit")) {
            1 -> replaceFragment(InformationFragment())
            2 -> replaceFragment(PasswordFragment())
            else -> showMessage("Error! An error occurred. Please try again later", this, false) {
                finish()
            }
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit()
    }
}