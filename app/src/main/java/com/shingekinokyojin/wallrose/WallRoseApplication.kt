package com.shingekinokyojin.wallrose

import android.app.Application
import com.shingekinokyojin.wallrose.data.AppContainer
import com.shingekinokyojin.wallrose.data.DefaultAppContainer
import com.shingekinokyojin.wallrose.utils.SharedPreferencesManager

class WallRoseApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
        SharedPreferencesManager.init(this)
    }
}