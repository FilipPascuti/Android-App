package com.ilazar.myapp.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val _id: String,
    @ColumnInfo(name = "text")
    var text: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "length")
    var length: Int,
    @ColumnInfo(name = "liked")
    var liked: Boolean
) {
    override fun toString(): String = text
}
