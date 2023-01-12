package com.onix77.sklad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "elements")
data class ElementDB(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "category")
    var nameCat: String,
    @ColumnInfo(name = "element")
    var nameEl: String,
    @ColumnInfo(name = "rest")
    var number: Int,
    @ColumnInfo(name = "critical_rest")
    var criticalRest: Int
) :java.io.Serializable

@Entity(tableName = "history")
data class EntryHistory(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "category")
    var nameCat: String,
    @ColumnInfo(name = "element")
    var nameEl: String,
    @ColumnInfo(name = "changeRest")
    var changeRest: String,
    @ColumnInfo(name = "rest")
    var rest: Int
)