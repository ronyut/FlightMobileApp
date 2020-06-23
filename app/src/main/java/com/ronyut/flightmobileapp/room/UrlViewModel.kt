package com.ronyut.flightmobileapp.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
    This class is the ViewModel of the room db
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class UrlViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UrlRepository
    val allUrls: LiveData<List<Url>>

    init {
        val urlsDao = UrlRoomDB.getDatabase(application, viewModelScope).urlDao()
        repository = UrlRepository(urlsDao)
        allUrls = repository.allUrls
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    // insert a new url to the db
    fun insert(url: Url) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(url)
    }
}