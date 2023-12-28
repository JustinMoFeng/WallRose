package com.shingekinokyojin.wallrose

import android.app.Application
import com.shingekinokyojin.wallrose.data.AppContainer
import com.shingekinokyojin.wallrose.data.DefaultAppContainer
import com.shingekinokyojin.wallrose.utils.SharedPreferencesManager

class WallRoseApplication : Application() {

    lateinit var container: AppContainer

    companion object {
        lateinit var instance: WallRoseApplication
    }

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
        instance = this
        SharedPreferencesManager.init(this)
    }
}