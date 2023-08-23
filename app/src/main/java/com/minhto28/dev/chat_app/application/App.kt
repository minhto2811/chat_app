package com.minhto28.dev.chat_app.application

import android.app.Application
import com.google.gson.Gson

class App : Application() {
    var gSon: Gson? = null
    override fun onCreate() {
        super.onCreate()
        mSelf = this
        gSon = Gson()
    }


    companion object {
        private var mSelf: App? = null
        fun self(): App? {
            return mSelf
        }
    }

}
