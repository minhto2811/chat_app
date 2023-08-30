package com.minhto28.dev.chat_app.ui.auth.register

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.DATA
import com.minhto28.dev.chat_app.utils.SharedPrefs
import com.minhto28.dev.chat_app.utils.generateUniqueID

class RegisterViewModel : ViewModel() {

    private val databaseReference = Firebase.database.reference
    fun register(
        uri: Uri,
        fullname: String,
        username: String,
        password: String,
        callback: (Boolean?) -> Unit
    ) {
        databaseReference.child("account").child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        saveImage(uri, username, password, fullname, callback)
                    } else {
                        callback.invoke(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(null)
                }

            })
    }


    private fun saveImage(
        uri: Uri,
        username: String,
        password: String,
        fullname: String,
        callback: (Boolean?) -> Unit
    ) {

        val uid = generateUniqueID()
        val ref = FirebaseStorage.getInstance().reference.child("avatar/uid_${uid}.jpg")
        val uploadTask = ref.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.e("saveImage exception", it.toString())
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                createUser(downloadUri, uid, username, password, fullname, callback)
            } else {
                callback.invoke(null)
            }
        }
    }

    private fun createUser(
        downloadUri: Uri,
        uid: String,
        username: String,
        password: String,
        fullname: String,
        callback: (Boolean?) -> Unit
    ) {
        val account = Account(uid, username, password)
        val user = User(uid, downloadUri.toString(), fullname, true)

        val accountRef = databaseReference.child("account").child(username)
        val userRef = databaseReference.child("user").child(uid)
        accountRef.setValue(account).addOnSuccessListener {
            userRef.setValue(user).addOnSuccessListener {
                SharedPrefs.instance.put(account)
                DATA(user, account)
                callback.invoke(true)
            }.addOnFailureListener {
                callback.invoke(null)
            }
        }.addOnFailureListener {
            callback.invoke(null)
        }

    }
}