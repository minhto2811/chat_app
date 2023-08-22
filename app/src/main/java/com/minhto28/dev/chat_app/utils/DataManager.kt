package com.minhto28.dev.chat_app.utils

import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User

class DataManager private constructor() {

    private var account: Account? = null
    private var user: User? = null

    fun setAccount(account: Account) {
        this.account = account
    }

    fun getAccount(): Account? {
        return account
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun getUser(): User? {
        return user
    }

    companion object {
        @Volatile
        private var instance: DataManager? = null

        fun getInstance(): DataManager {
            return instance ?: synchronized(this) {
                instance ?: DataManager().also { instance = it }
            }
        }
    }
}
