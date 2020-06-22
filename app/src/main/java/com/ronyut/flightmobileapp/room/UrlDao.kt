package com.ronyut.flightmobileapp.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UrlDao {
    @Query("SELECT * from url_table ORDER BY date DESC LIMIT 5")
    fun getLastUrls(): LiveData<List<Url>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(url: Url)
}