package com.minhto28.dev.chat_app.models

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable

data class User(
    val uid: String, val avatar: String, val fullname: String, val status: Boolean
) : Serializable {
    constructor() : this("", "", "", true) {
        // Không cần phải làm gì trong constructor này
    }

    fun setStatusOnline(isOnline:Boolean) {
        val status = Firebase.database.reference.child("user").child(uid).child("status")
        status.setValue(isOnline)
    }
}
