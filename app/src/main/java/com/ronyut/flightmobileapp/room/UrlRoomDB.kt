package com.ronyut.flightmobileapp.room

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Url::class), version = 1, exportSchema = false)
public abstract class UrlRoomDB : RoomDatabase() {
    abstract fun urlDao(): UrlDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: UrlRoomDB? = null

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

    private class UrlDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.urlDao())
                }
            }
        }

        suspend fun populateDatabase(urlDao: UrlDao) {
            val toAdd = urlDao.getLastUrls().value?.size ?: 5
            for (i in 0 until toAdd) {
                val url = Url("Sample ${i + 1}", "now")
                urlDao.insert(url)
            }
        }
    }
}