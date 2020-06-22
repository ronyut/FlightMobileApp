package com.ronyut.flightmobileapp.room

import androidx.lifecycle.LiveData

class UrlRepository(private val urlDao: UrlDao) {
    val allUrls: LiveData<List<Url>> = urlDao.getLastUrls()

    suspend fun insert(url: Url) {
        urlDao.insert(url)
    }
}