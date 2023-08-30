package com.minhto28.dev.chat_app.models

data class Message constructor(
    val time: Long = 0L,
    val message: String? = null,
    var image: List<String>?=null,
    val uid: String = "",
    val emotion: Int? = null,
    val seen: Boolean = false
)
