package com.ronyut.flightmobileapp.room

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
    This class is the Room database supervisor
    Author: Rony Utevsky
    Date: 23-06-2020
 */
@Database(entities = arrayOf(Url::class), version = 1, exportSchema = false)
public abstract class UrlRoomDB : RoomDatabase() {
    abstract fun urlDao(): UrlDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: UrlRoomDB? = null

        // Get the db instance
        fun getDatabase(context: Context, scope: CoroutineScope): UrlRoomDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UrlRoomDB::class.java,
                    "url_db"
                )
                    .addCallback(UrlDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    // This class opens and populates the db
    private class UrlDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        // Open the db
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.urlDao())
                }
            }
        }

        // Here we can populate the db with dummy data
        fun populateDatabase(urlDao: UrlDao) {
            val toAdd = urlDao.getLastUrls().value?.size ?: 5
        }
    }
}