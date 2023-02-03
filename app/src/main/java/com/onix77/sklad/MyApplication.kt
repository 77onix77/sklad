package com.onix77.sklad

import android.app.Application

class MyApplication : Application() {
    private val db by lazy { MainDB.getDB(this) }
    val repository by lazy { Repository(db.getDao()) }
}