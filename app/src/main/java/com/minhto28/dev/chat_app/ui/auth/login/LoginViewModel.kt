package com.minhto28.dev.chat_app.ui.auth.login

import com.minhto28.dev.chat_app.utils.SharedPrefs
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.DATA


class LoginViewModel : ViewModel() {
    val success: MutableLiveData<Boolean?> = MutableLiveData()
    private val databaseRefeernce: DatabaseReference = Firebase.database.reference
    fun login(username: String, password: String) {
        databaseRefeernce.child("account").child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val account = snapshot.getValue(Account::class.java)
                    if (account != null && account.password == password) {
                        databaseRefeernce.child("user").child(account.uid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(User::class.java)
                                    if (user != null) {
                                        SharedPrefs.instance.put(account)
                                        DATA(user, account)
                                        success.value = true
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    success.value = null
                                }

                            })
                    } else {
                        success.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    success.value = null
                }

            })
    }
}