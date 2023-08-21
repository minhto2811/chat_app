package com.minhto28.dev.chat_app.models

import android.os.Parcelable
import java.io.Serializable


data class Account(
    val uid: String,
    val username: String,
    val password: String,
) : Serializable
