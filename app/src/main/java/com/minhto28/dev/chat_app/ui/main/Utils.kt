package com.minhto28.dev.chat_app.ui.main

import androidx.lifecycle.MutableLiveData
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.Friend
import com.minhto28.dev.chat_app.models.User


val dataHome = MutableLiveData<HashMap<String, User>>()
val dataFriend = MutableLiveData<HashMap<String, Friend>>()
val dataInvitation = MutableLiveData<HashMap<String, User>>()
val USER = MutableLiveData<User>()
var ACCOUNT = MutableLiveData<Account>()

fun DATA(user: User, account: Account) {
    USER.postValue(user)
    ACCOUNT.postValue(account)
}