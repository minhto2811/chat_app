package com.minhto28.dev.chat_app.models

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable

data class User constructor(
    val uid: String = "",
    val avatar: String = "",
    val fullname: String = "",
    val status: Boolean = true,
    var cache: Boolean = false
) : Serializable {
    private val myRef = Firebase.database.reference
    private val storageRef = FirebaseStorage.getInstance().reference
    fun setStatusOnline(isOnline: Boolean) {
        val status = myRef.child("user").child(uid).child("status")
        status.setValue(isOnline)
    }

    fun sendFriendInvitations(id: String, callback: ((Boolean) -> Unit)) {
        myRef.child("invitation").child(uid).child(id).setValue(id).addOnSuccessListener {
            callback.invoke(true)
        }.addOnFailureListener {
            callback.invoke(false)
        }
        myRef.child("cache").child(id).child(uid).setValue(uid)
    }

    fun delFriendInvitations(id: String, callback: ((Boolean) -> Unit)) {
        myRef.child("invitation").child(uid).child(id).removeValue().addOnSuccessListener {
            callback.invoke(true)
        }.addOnFailureListener {
            callback.invoke(false)
        }
        myRef.child("cache").child(id).child(uid).removeValue()
    }


    fun denyFriendInvitations(id: String) {
        myRef.child("invitation").child(id).child(uid).removeValue()
        myRef.child("cache").child(uid).child(id).removeValue()
    }

    fun addFriend(id: String) {
        //xác nhận lời mời
        denyFriendInvitations(id)
        //Thêm bạn bè
        myRef.child("friend").child(uid).child(id).setValue(Friend(idFriend = id))
        myRef.child("friend").child(id).child(uid).setValue(Friend(idFriend = uid))
    }


    fun changeAvatat(uri: Uri, callback: (Boolean) -> Unit) {
        val ref = storageRef.child("avatar/uid_${uid}.jpg")
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
                val downloadUri = task.result.toString()
                val ava = myRef.child("user/$uid/avatar")
                ava.setValue(downloadUri).addOnFailureListener {
                    callback.invoke(false)
                }.addOnSuccessListener {
                    callback.invoke(true)
                }
            } else {
                callback.invoke(false)
            }
        }
    }

    fun update(newFullname: String, callback: (Boolean) -> Unit) {
        myRef.child("user/$uid/fullname").setValue(newFullname).addOnSuccessListener {
            callback.invoke(true)
        }.addOnFailureListener {
            callback.invoke(false)
        }
    }

    fun deleteFriend(idReciver: String, callback: () -> Unit) {
        myRef.child("friend").child(uid).child(idReciver).removeValue()
        myRef.child("friend").child(idReciver).child(uid).removeValue()
        deleteAllFilesInFolder("chat/${uid + idReciver}")
        deleteAllFilesInFolder("chat/${idReciver + uid}")
        clearChat(idReciver, false) {
            callback.invoke()
        }
    }

    fun clearChat(idReciver: String, only: Boolean, callback: () -> Unit) {
        myRef.child("chat").child(uid).child(idReciver).removeValue()
        if (!only) {
            myRef.child("chat").child(idReciver).child(uid).removeValue()
        }
        callback.invoke()
    }

    private fun deleteAllFilesInFolder(folderPath: String) {
        val folderRef = storageRef.child(folderPath)
        folderRef.listAll().addOnSuccessListener { listResult ->
            val deletePromises = listResult.items.map { item ->
                item.delete()
            }
            val allDeletes = Tasks.whenAll(deletePromises)
            allDeletes.addOnSuccessListener {
                Log.e("deleteAllFilesInFolder $folderPath", "success")
            }.addOnFailureListener { exception ->
                Log.e("deleteAllFilesInFolder $folderPath", exception.message.toString())
            }
        }.addOnFailureListener { exception ->
            Log.e("deleteAllFilesInFolder $folderPath", exception.message.toString())
        }
    }

    fun setSeeding(id: String, seeding: Boolean) {
        myRef.child("friend").child(uid).child(id).child("seending").setValue(seeding)
        if (seeding) {
            myRef.child("friend").child(uid).child(id).child("count").setValue(0)
        }
    }


}

