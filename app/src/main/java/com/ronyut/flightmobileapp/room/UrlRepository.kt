package com.ronyut.flightmobileapp.room

import androidx.lifecycle.LiveData

/*
    The URL repository
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class UrlRepository(private val urlDao: UrlDao) {
    val allUrls: LiveData<List<Url>> = urlDao.getLastUrls()

    // insert a url to the DAO
    suspend fun insert(url: Url) {
        urlDao.insert(url)
    }
}