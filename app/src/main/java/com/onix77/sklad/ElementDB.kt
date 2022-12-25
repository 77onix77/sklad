package com.onix77.sklad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
)
