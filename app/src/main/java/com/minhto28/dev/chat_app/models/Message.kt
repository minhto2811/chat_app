package com.minhto28.dev.chat_app.models

data class Message(
    val time: Long?, val message: String?, val image: String?, val uid: String?, val emotion: Int?
) {
    constructor() : this(null, null, null, null, null) {
        // Không cần phải làm gì trong constructor này
    }
}
