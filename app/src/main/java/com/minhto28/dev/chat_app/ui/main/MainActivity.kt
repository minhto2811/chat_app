package com.minhto28.dev.chat_app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.ActivityMainBinding
import com.minhto28.dev.chat_app.service.MyService
import com.minhto28.dev.chat_app.utils.showMessage

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var intent: Intent

    companion object {
        private lateinit var binding: ActivityMainBinding
        var countNow = 0

        fun addCount(id: Int, num: Int, invitation: Int) {
            countNow += num
            if (countNow + invitation > 0) {
                val badge = binding.bottomNavigation.getOrCreateBadge(id)
                badge.isVisible = true
                badge.number = countNow + invitation
            } else {
                clearCount(id)
            }
        }

        fun clearCount(id: Int) {
            val badgeDrawable = binding.bottomNavigation.getBadge(id)
            if (badgeDrawable != null) {
                badgeDrawable.isVisible = false
                badgeDrawable.clearNumber()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent = Intent(this,MyService::class.java)
        startService(intent)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigation, navController)
    }


    override fun onBackPressed() {
        showMessage("Exit app", this, true) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intent)
    }

}