package com.minhto28.dev.chat_app.models

data class Friend(
    val idFriend: String = "",
    val lastMessage: String? = null,
    val count: Int = 0,
    var user: User? = null,
    var seending:Boolean = false
)
