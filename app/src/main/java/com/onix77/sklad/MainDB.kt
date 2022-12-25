package com.onix77.sklad

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [ElementDB::class], version = 1)
abstract class MainDB : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        fun getDB(context: Context): MainDB {
            return Room.databaseBuilder(context, MainDB::class.java, "Sklad.db").build()
        }
    }
}