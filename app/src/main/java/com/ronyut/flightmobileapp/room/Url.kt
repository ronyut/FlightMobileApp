package com.ronyut.flightmobileapp.room

import androidx.room.*

@Entity(tableName = "url_table")
data class Url(
    @PrimaryKey @ColumnInfo(name = "url") val url: String,
    val date: String
) {}