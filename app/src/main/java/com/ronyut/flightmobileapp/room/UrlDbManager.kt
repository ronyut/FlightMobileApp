package com.ronyut.flightmobileapp.room

import android.content.Context
import java.io.File
import java.io.PrintWriter

/*
    This class manages the database of the URLs
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class UrlDbManager(context: Context) {
    private val db: File = File(
        context.filesDir,
        DATABASE_FILE
    )

    companion object {
        const val DATABASE_FILE = "urlDb"
        const val MAX_URLS = 5
    }

    init {
        // create the database of it doesn't already exist
        db.createNewFile()
    }

    // Add a url to the database
    fun addUrl(entry: String) {
        val rawDb = getDb()
        val newDb = listOf(entry) + rawDb.filter { x -> x != entry }.takeLast(MAX_URLS)
        truncateDb()
        db.appendText(newDb.joinToString())
    }

    // Retrieve the database
    fun getDb(): List<String> =
        db.readText(Charsets.UTF_8).split(", ").toList().filter { it != "" }

    // Truncate the database
    private fun truncateDb() {
        val writer = PrintWriter(db)
        writer.print("")
        writer.close()
    }
}