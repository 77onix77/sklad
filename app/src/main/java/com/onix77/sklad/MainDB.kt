package com.onix77.sklad

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [ElementDB::class, EntryHistory::class], version = 3, exportSchema = true,
    autoMigrations = [AutoMigration(2, 3)]
)
abstract class MainDB : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        fun getDB(context: Context): MainDB {
            return Room.databaseBuilder(context, MainDB::class.java, "Sklad.db")
                .build()
        }
    }
}