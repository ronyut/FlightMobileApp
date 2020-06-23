package com.ronyut.flightmobileapp

import android.content.Context
import java.io.File
import java.io.PrintWriter

class UrlDbManager(context: Context) {
    private val db: File = File(context.filesDir, DATABASE_FILE)

    companion object {
        const val DATABASE_FILE = "urlDb"
        const val MAX_URLS = 5
    }

    init {
        db.createNewFile()
    }

    fun addUrl(entry: String) {
        val rawDb = getDb()
        val newDb = listOf(entry) + rawDb.filter { x -> x != entry }.takeLast(MAX_URLS)
        truncateDb()
        db.appendText(newDb.joinToString())
    }

    fun getDb(): List<String> =
        db.readText(Charsets.UTF_8).split(", ").toList().filter { it != "" }

    private fun truncateDb() {
        val writer = PrintWriter(db)
        writer.print("")
        writer.close()
    }
}