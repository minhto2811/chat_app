package com.minhto28.dev.chat_app.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.dataHome

class HomeViewModel : ViewModel() {
    fun filter(query: String) {
        if (query.isEmpty()) {
            dataLiveData.postValue(dataHome.value)
        } else {
            val map = HashMap<String, User>()
            dataHome.value?.map {
                if (it.value.fullname!!.lowercase()
                        .contains(query.lowercase()) || it.value.uid!!.contains(query)
                ) {
                    map[it.value.uid!!] = it.value
                }
            }
            dataLiveDataFilter.postValue(map)
        }
    }

    var dataLiveData = dataHome
    var dataLiveDataFilter = MutableLiveData<HashMap<String, User>>()
}