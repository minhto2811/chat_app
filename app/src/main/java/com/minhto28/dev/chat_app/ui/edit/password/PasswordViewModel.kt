package com.minhto28.dev.chat_app.ui.edit.password

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.ui.main.ACCOUNT
import com.minhto28.dev.chat_app.utils.SharedPrefs

class PasswordViewModel : ViewModel() {
    private val myRef = Firebase.database.reference
    fun update(passwordOld: String, passwordNew: String, callback: (Boolean?) -> Unit) {
        val checkPassword = myRef.child("account").child(ACCOUNT.value!!.username)
        checkPassword.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val account = snapshot.getValue(Account::class.java)
                if (account == null || account.password != passwordOld) {
                    callback.invoke(false)
                    return
                }
                val updatePassword =
                    myRef.child("account").child(ACCOUNT.value!!.username).child("password")
                updatePassword.setValue(passwordNew).addOnSuccessListener {
                    account.password = passwordNew
                    SharedPrefs.instance.put(account)
                    callback.invoke(true)
                }.addOnFailureListener {
                    callback.invoke(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.invoke(null)
            }

        })
    }
}