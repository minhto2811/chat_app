package com.minhto28.dev.chat_app.models

import android.net.Uri

data class Message(
    val time: Long?,
    val message: String?,
    var image: List<String>?,
    val uid: String?,
    val emotion: Int?,
    val seen: Boolean?
) {
    constructor() : this(null, null, null, null, null, true) {
        // Không cần phải làm gì trong constructor này
    }
}
