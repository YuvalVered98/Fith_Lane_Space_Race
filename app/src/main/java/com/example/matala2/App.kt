package com.example.matala2


import android.app.Application
import com.example.matala2.utilities.SharedPreferencesManagerV3
import com.example.matala2.utilities.SignalManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManagerV3.init(this)
        SignalManager.init(this)
    }
}