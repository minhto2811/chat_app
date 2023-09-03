package com.minhto28.dev.chat_app.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
        private var countNow = 0

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

        private fun clearCount(id: Int) {
            val badgeDrawable = binding.bottomNavigation.getBadge(id)
            if (badgeDrawable != null) {
                badgeDrawable.isVisible = false
                badgeDrawable.clearNumber()
            }
        }
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
                binding.bottomNavigation.selectedItemId = R.id.friendsFragment
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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

        val filter = IntentFilter("com.example.ACTION_NOTIFICATION_CLICKED")
        registerReceiver(notificationReceiver, filter)
    }


    override fun onBackPressed() {
        showMessage("Exit app", this, true) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intent)
        unregisterReceiver(notificationReceiver)
    }

}