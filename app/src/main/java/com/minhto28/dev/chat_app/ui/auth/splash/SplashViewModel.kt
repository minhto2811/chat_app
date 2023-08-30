package com.minhto28.dev.chat_app.ui.auth.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.DATA

class SplashViewModel : ViewModel() {
    val success: MutableLiveData<Boolean> = MutableLiveData()

    private val databaseReference = Firebase.database.reference
    fun login(account: Account) {
        val accountRef = databaseReference.child("account").child(account.username)
        accountRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataAccount = snapshot.getValue(Account::class.java)
                if (dataAccount != null && dataAccount.password == account.password) {
                    getUser(dataAccount)
                } else {
                    success.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                success.value = false
            }
        })
    }

    private fun getUser(account: Account) {
        val usersRef = databaseReference.child("user").child(account.uid)
        usersRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        DATA(user, account)
                        success.value = true
                    } else {
                        success.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    success.value = false
                }

            }
        )
    }
}