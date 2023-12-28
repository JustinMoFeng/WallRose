package com.shingekinokyojin.wallrose

import android.app.Application
import com.qweather.sdk.view.HeConfig
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
        HeConfig.init("HE2312290206121152", "f2313521f7bd47daa1805d5106b28898")
        //切换至免费订阅
        HeConfig.switchToDevService()
    }
}