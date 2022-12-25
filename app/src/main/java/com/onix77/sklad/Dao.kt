package com.onix77.sklad

import androidx.room.Insert
import androidx.room.Query

@androidx.room.Dao
interface Dao {
    @Insert
    fun insertEl(item: ElementDB)



    @Query("SELECT DISTINCT category FROM elements")
    fun getCat(): List<String>

    @Query("SELECT * FROM elements")
    fun getAll(): List<ElementDB>
}