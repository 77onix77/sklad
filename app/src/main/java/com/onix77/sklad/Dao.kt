package com.onix77.sklad


import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@androidx.room.Dao
interface Dao {
    @Insert
    suspend fun insertEl(item: ElementDB)

    @Delete
    suspend fun delete(elementDB: ElementDB)

    @Update
    suspend fun updateEl(elementDB: ElementDB)

    @Query("SELECT DISTINCT category FROM elements")
    fun getCat(): Flow<List<String>>

    @Query("SELECT DISTINCT category FROM elements")
    suspend fun getCatL(): List<String>

    @Query("SELECT * FROM elements")
    suspend fun getAll(): List<ElementDB>

    @Query("SELECT * FROM elements WHERE category = :cat")
    fun getEl(cat: String): Flow<List<ElementDB>>

    @Insert
    suspend fun insertInHistory(item: EntryHistory)

    @Query("SELECT element FROM elements WHERE category = :cat")
    fun getNameEl(cat: String): Flow<List<String>>

    @Query("SELECT element FROM elements WHERE category = :cat")
    suspend fun getNameElL(cat: String): List<String>

    @Query("SELECT * FROM history WHERE date(date) BETWEEN date(:fromDate) AND date(:toDate)")
    fun getAllDateHis(fromDate: String, toDate: String): Flow<List<EntryHistory>>

    @Query("SELECT * FROM history WHERE date(date) BETWEEN date(:fromDate) AND date(:toDate) AND category = :cat")
    fun getCatDateHis(fromDate: String, toDate: String, cat: String): Flow<List<EntryHistory>>

    @Query("SELECT * FROM history WHERE date(date) BETWEEN date(:fromDate) AND date(:toDate) AND category = :cat AND element = :el")
    fun getElDateHis(fromDate: String, toDate: String, cat: String, el: String): Flow<List<EntryHistory>>
}