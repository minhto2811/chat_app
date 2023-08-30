package com.minhto28.dev.chat_app.ui.edit.information

import androidx.lifecycle.ViewModel
import com.minhto28.dev.chat_app.ui.main.USER

class InformationViewModel : ViewModel() {
    fun update(fullname: String, callback: (Boolean) -> Unit) {
        USER.value?.update(fullname) {
            callback.invoke(it)
        }
    }
}