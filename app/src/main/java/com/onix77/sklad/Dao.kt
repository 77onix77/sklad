package com.onix77.sklad

//import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@androidx.room.Dao
interface Dao {
    @Insert
    fun insertEl(item: ElementDB)

    //@Delete
    //fun delete(elementDB: ElementDB)

    @Update
    fun updateEl(elementDB: ElementDB)

    @Query("SELECT DISTINCT category FROM elements")
    fun getCat(): List<String>

    @Query("SELECT * FROM elements")
    fun getAll(): List<ElementDB>

    @Query("SELECT * FROM elements WHERE category = :cat")
    fun getEl(cat: String): List<ElementDB>
}