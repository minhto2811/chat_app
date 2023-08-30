package com.minhto28.dev.chat_app.ui.main.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.minhto28.dev.chat_app.ui.main.USER

class SettingsViewModel : ViewModel() {
    fun changeAvatar(uri: Uri, callback: (Boolean) -> Unit) {
        USER.value?.changeAvatat(uri) {
            callback.invoke(it)
        }
    }
}