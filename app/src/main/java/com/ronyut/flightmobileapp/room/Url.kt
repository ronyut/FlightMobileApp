package com.ronyut.flightmobileapp.room

import androidx.room.*
/*
    This class hold the url entity of the url table in the room db
    Author: Rony Utevsky
    Date: 23-06-2020
 */
@Entity(tableName = "url_table")
data class Url(
    @PrimaryKey @ColumnInfo(name = "url") val url: String,
    val date: String
)