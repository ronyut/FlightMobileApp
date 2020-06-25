package com.ronyut.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ronyut.flightmobileapp.api.PicassoHandler
import com.ronyut.flightmobileapp.room.UrlDbManager
import kotlinx.android.synthetic.main.activity_main.*

/*
    The main activity: connect to a server
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MESSAGE = "com.ronyut.flightmobileapp.URL"
        const val NO_ERROR = ""
    }

    private lateinit var toaster: Toaster
    private lateinit var db: UrlDbManager
    private var baseUrl = ""
    private var baseUrlLiteral = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the annoying and redundant action bar
        supportActionBar?.hide()
        // Set up the room database manager that will hold the urls
        db = UrlDbManager(applicationContext)
        // create the toaster that manages all toasts
        toaster = Toaster(applicationContext)
        // update the buttons with the urls from db
        updateView()
        // set the listeners
        setListeners()
    }

    /*
    Check if the server is up
     */
    private fun checkConnection() {
        val picasso = PicassoHandler(this)
        picasso.getImage(baseUrl, screenshotTest) { msg ->
            when {
                msg != null -> {
                    toast("Server unavailable")
                    toggleButton(false)
                }
                else -> connectionSuccessful()
            }
        }
    }

    /*
    If the server is online, go to the dashboard
     */
    private fun connectionSuccessful() {
        toast("Connected! Loading...")
        saveUrlToDb()
        // move to dashboard activity
        moveToDashboard()
        toggleButton(false)
    }

    /*
    Toggle the connect button
     */
    private fun toggleButton(disable: Boolean) {
        if (disable) {
            connectBtn.isEnabled = false
            connectBtn.text = getString(R.string.connect_btn_text_please_wait)
        } else {
            connectBtn.isEnabled = true
            connectBtn.text = getString(R.string.home_btn_connect)
        }
    }

    /*
    Save the url in the database
     */
    private fun saveUrlToDb() {
        db.addUrl(baseUrlLiteral)
        updateView()
    }

    // Update the buttons with the urls from db
    private fun updateView() {
        val urls = db.getDb()
        var last = 0
        for ((i, url) in urls.withIndex()) {
            when (i + 1) {
                1 -> setBtn(btn1, url)
                2 -> setBtn(btn2, url)
                3 -> setBtn(btn3, url)
                4 -> setBtn(btn4, url)
                5 -> setBtn(btn5, url)
            }
            last = i + 1
        }

        // Handle remaining buttons
        for (i in last + 1 until 6) {
            when (i) {
                1 -> setBtn(btn1, "", View.GONE)
                2 -> setBtn(btn2, "", View.GONE)
                3 -> setBtn(btn3, "", View.GONE)
                4 -> setBtn(btn4, "", View.GONE)
                5 -> setBtn(btn5, "", View.GONE)
            }
        }
    }

    // Set button text and visibility
    private fun setBtn(btn: Button, url: String, view: Int = View.VISIBLE) {
        btn.text = url
        btn.visibility = view
    }

    /*
    Move to the next activity
     */
    private fun moveToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, baseUrl)
        }
        startActivity(intent)
    }

    // Set all the listeners on this activity
    private fun setListeners() {
        setConnectBtnListener()
        setUrlButtonListeners()
    }

    // Set the listener for the url buttons
    private fun setUrlButtonListeners() {
        btn1.setOnClickListener { textboxNewServer.setText(btn1.text) }
        btn2.setOnClickListener { textboxNewServer.setText(btn2.text) }
        btn3.setOnClickListener { textboxNewServer.setText(btn3.text) }
        btn4.setOnClickListener { textboxNewServer.setText(btn4.text) }
        btn5.setOnClickListener { textboxNewServer.setText(btn5.text) }
    }

    // Set the connection button listener
    private fun setConnectBtnListener() {
        connectBtn.setOnClickListener {
            val url = textboxNewServer.text.toString()
            baseUrl = Util.replaceLocalhost(url)
            baseUrlLiteral = url

            toggleButton(true)

            when (val err = Util.checkUrlValidity(baseUrl)) {
                NO_ERROR -> checkConnection()
                else -> {
                    toast(err)
                    toggleButton(false)
                }
            }
        }
    }

    /*
    Make a toast
     */
    private fun toast(text: String?) {
        toaster.toast(text)
    }
}