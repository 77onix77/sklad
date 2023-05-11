package com.onix77.sklad

import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds

class MyApplication : Application() {
    private val db by lazy { MainDB.getDB(this) }
    val repository by lazy { Repository(db.getDao()) }

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {
            Log.d("MyLog", "YandexAds initialize")
        }
    }
}