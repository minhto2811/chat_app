package com.minhto28.dev.chat_app.ui.main.friends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.dataFriend
import com.minhto28.dev.chat_app.ui.main.dataInvitation

class FriendViewModel : ViewModel() {
    val dataLiveData = dataFriend
    val dataLiveDataInvitation = dataInvitation

    fun filter(query: String) {
        if (query.isEmpty()) {
            dataLiveData.postValue(dataFriend.value)
        } else {
            val map = HashMap<String, User>()
            dataFriend.value?.map {
                if (it.value.fullname!!.lowercase()
                        .contains(query.lowercase()) || it.value.uid!!.contains(query)
                ) {
                    map[it.value.uid!!] = it.value
                }
            }
            dataLiveDataFilter.postValue(map)
        }
    }

    val dataLiveDataFilter = MutableLiveData<HashMap<String, User>>()
}