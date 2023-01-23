package com.onix77.sklad

//import androidx.room.Delete
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
//import kotlinx.coroutines.flow.Flow


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

    @Insert
    fun insertInHistory(item: EntryHistory)  // добавляет запись в таблицу истории

    @Query("SELECT element FROM elements WHERE category = :cat")
    fun getNameEl(cat: String): List<String>

    @Query("SELECT * FROM history")
    fun getAllHis(): List<EntryHistory>

    @Query("SELECT * FROM history WHERE date(date) BETWEEN date(:fromDate) AND date(:toDate)")
    fun getAllDateHis(fromDate: String, toDate: String): List<EntryHistory>

    @Query("SELECT * FROM history WHERE date(date) BETWEEN date(:fromDate) AND date(:toDate) AND category = :cat")
    fun getCatDateHis(fromDate: String, toDate: String, cat: String): List<EntryHistory>

    @Query("SELECT * FROM history WHERE date(date) BETWEEN date(:fromDate) AND date(:toDate) AND category = :cat AND element = :el")
    fun getElDateHis(fromDate: String, toDate: String, cat: String, el: String): List<EntryHistory>
}