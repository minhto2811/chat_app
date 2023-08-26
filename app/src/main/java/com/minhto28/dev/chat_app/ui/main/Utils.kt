package com.minhto28.dev.chat_app.ui.main

import androidx.lifecycle.MutableLiveData
import com.minhto28.dev.chat_app.models.User


val dataHome = MutableLiveData<ArrayList<User>>()
val dataFriend = MutableLiveData<ArrayList<User>>()
val dataInvitation = MutableLiveData<ArrayList<User>>()