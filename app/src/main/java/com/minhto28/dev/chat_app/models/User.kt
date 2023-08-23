package com.minhto28.dev.chat_app.models

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable

data class User(
    val uid: String?,
    val avatar: String?,
    val fullname: String?,
    val status: Boolean,
    val friends: HashMap<String, String>?
) : Serializable {
    constructor() : this(null, null, null, true, null) {
        // Không cần phải làm gì trong constructor này
    }

    fun setStatusOnline(isOnline: Boolean) {
        val status = Firebase.database.reference.child("user").child(uid!!).child("status")
        status.setValue(isOnline)
    }

    fun sendFriendInvitations(id: String, callback: ((Boolean) -> Unit)) {
        val send = Firebase.database.reference.child("invitation").child(uid!!).child(id)
        send.setValue(id).addOnSuccessListener {
            callback.invoke(true)
        }.addOnFailureListener {
            callback.invoke(false)
        }
    }

    fun addFriend(id: String) {
        //xác nhận lời mời
        val confirm = Firebase.database.reference.child("invitation").child(id).child(uid!!)
        confirm.removeValue()
        //Thêm bạn bè
        val yourAdd =
            Firebase.database.reference.child("user").child(uid!!).child("friends").child(id)
        yourAdd.setValue(id)
        val myAdd =
            Firebase.database.reference.child("user").child(id).child("friends").child(uid!!)
        myAdd.setValue(uid!!)
    }

    fun delFriend(id: String) {
        val myDel =
            Firebase.database.reference.child("user").child(uid!!).child("friends").child(id)
        myDel.removeValue()
        val yourDel =
            Firebase.database.reference.child("user").child(id).child("friends").child(uid!!)
        yourDel.removeValue()
    }
}
