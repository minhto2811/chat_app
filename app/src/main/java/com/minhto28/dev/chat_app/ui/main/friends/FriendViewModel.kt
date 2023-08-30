package com.minhto28.dev.chat_app.ui.main.friends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minhto28.dev.chat_app.models.Friend
import com.minhto28.dev.chat_app.ui.main.dataFriend
import com.minhto28.dev.chat_app.ui.main.dataInvitation

class FriendViewModel : ViewModel() {
    val dataLiveData = dataFriend
    val dataLiveDataInvitation = dataInvitation

    fun filter(query: String) {
        if (query.isEmpty()) {
            dataLiveData.postValue(dataFriend.value)
        } else {
            val map = HashMap<String, Friend>()
            dataFriend.value?.map {
                if (it.value.user?.fullname!!.lowercase()
                        .contains(query.lowercase()) || it.value.idFriend.contains(query)
                ) {
                    map[it.value.idFriend] = it.value
                }
            }
            dataLiveDataFilter.postValue(map)
        }
    }

    val dataLiveDataFilter = MutableLiveData<HashMap<String, Friend>>()
}