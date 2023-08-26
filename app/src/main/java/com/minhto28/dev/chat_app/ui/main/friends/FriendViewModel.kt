package com.minhto28.dev.chat_app.ui.main.friends

import androidx.lifecycle.ViewModel
import com.minhto28.dev.chat_app.ui.main.dataFriend
import com.minhto28.dev.chat_app.ui.main.dataInvitation

class FriendViewModel : ViewModel() {
    val dataLiveData = dataFriend
    val dataLiveDataInvitation = dataInvitation
}