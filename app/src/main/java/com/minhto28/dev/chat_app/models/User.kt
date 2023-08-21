package com.minhto28.dev.chat_app.models

import java.io.Serializable

data class User(
    val uid: String, val avatar: String, val fullname: String, val status: Boolean
): Serializable
