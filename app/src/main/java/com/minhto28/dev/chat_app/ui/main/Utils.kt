package com.minhto28.dev.chat_app.ui.main

import androidx.lifecycle.MutableLiveData
import com.minhto28.dev.chat_app.models.User


val dataHome = MutableLiveData<HashMap<String, User>>()
val dataFriend = MutableLiveData<HashMap<String, User>>()
val dataInvitation = MutableLiveData<HashMap<String, User>>()
