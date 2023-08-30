package com.minhto28.dev.chat_app.models

import java.io.Serializable


data class Account(
    val uid: String="",
    val username: String="",
    var password: String="",
) : Serializable
