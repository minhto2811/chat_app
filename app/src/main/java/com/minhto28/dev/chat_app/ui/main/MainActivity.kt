package com.minhto28.dev.chat_app.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.ActivityMainBinding
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.DataManager
import com.minhto28.dev.chat_app.utils.showMessage

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = DataManager.getInstance().getUser()!!
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigation, navController)
    }


    override fun onBackPressed() {
        showMessage("Exit app", this, true) {
            user.setStatusOnline(false)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        user.setStatusOnline(true)
    }
    override fun onStop() {
        super.onStop()
        user.setStatusOnline(false)
    }


}