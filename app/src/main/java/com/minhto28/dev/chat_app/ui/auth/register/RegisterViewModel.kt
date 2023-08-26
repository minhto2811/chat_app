package com.minhto28.dev.chat_app.ui.auth.register

import SharedPrefs
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.DataManager
import com.minhto28.dev.chat_app.utils.generateUniqueID

class RegisterViewModel : ViewModel() {
    val success: MutableLiveData<Boolean?> = MutableLiveData()

    private val databaseReference = Firebase.database.reference
    fun register(uri: Uri, fullname: String, username: String, password: String) {
        databaseReference.child("account").child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        saveImage(uri, username, password, fullname)
                    } else {
                        success.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    success.value = null
                }

            })
    }


    private fun saveImage(uri: Uri, username: String, password: String, fullname: String) {

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
                createUser(downloadUri, uid, username, password, fullname)
            } else {
                success.value = null
            }
        }
    }

    private fun createUser(
        downloadUri: Uri,
        uid: String,
        username: String,
        password: String,
        fullname: String
    ) {
        val account = Account(uid, username, password)
        val user = User(uid, downloadUri.toString(), fullname, true, null)

        val accountRef = databaseReference.child("account").child(username)
        val userRef = databaseReference.child("user").child(uid)
        accountRef.setValue(account).addOnSuccessListener {
            userRef.setValue(user).addOnSuccessListener {
                SharedPrefs.instance.put(account)
                DataManager.getInstance().setAccount(account)
                DataManager.getInstance().setUser(user)
                success.value = true
            }.addOnFailureListener {
                success.value = null
            }
        }.addOnFailureListener {
            success.value = null
        }

    }
}