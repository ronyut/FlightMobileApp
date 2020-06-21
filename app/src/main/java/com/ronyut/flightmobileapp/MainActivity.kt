package com.ronyut.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil.isValidUrl
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.ronyut.flightmobileapp.API.FlightData
import com.ronyut.flightmobileapp.API.PicassoHandler
import com.ronyut.flightmobileapp.API.RequestHandler
import com.ronyut.flightmobileapp.API.ServerUpException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        const val EXTRA_MESSAGE = "com.ronyut.flightmobileapp.URL"
    }

    private var baseUrl = ""
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
        supportActionBar?.hide()

        setConnectBtnListener()
    }

    // set the connection button listener
    private fun setConnectBtnListener() {
        connectBtn.setOnClickListener {
            baseUrl = textboxNewServer.text.toString()
            toggleButton(true)

            if (checkUrlValidity()) checkConnection()
        }
    }

    /*
    Check if url is valid and show toast
     */
    private fun checkUrlValidity(): Boolean {
        var err = ""

        if (!isValidUrl(baseUrl)) {
            err = "Invalid URL"
        } else if (baseUrl.last().toString() == "/") {
            err = "Please remove trailing slash from URL"
        }

        if (err != "") {
            toast(err)
            toggleButton(false)
        }

        return err == ""
    }

    /*
    Check if the server is up
     */
    private fun checkConnection() {
        launch {
            PicassoHandler.getImage(baseUrl, screenshotTest) { msg ->
                toggleButton(false)

                if (msg != null) {
                    toast(msg) // TODO: changed to "server offline"
                } else {
                    saveUrl()
                    // TODO: refresh buttons - url updates

                    connectionSuccessful("200")
                }
            }
        }
    }

    /*
    If the server is online, go to the dashboard
     */
    private fun connectionSuccessful(code: String?) {
        toggleButton(false)
        // move to dashboard activity
        moveToDashboard()
    }

    /*
    Toggle the connect button
     */
    private fun toggleButton(disable: Boolean) {
        if (disable) {
            connectBtn.isEnabled = false
            connectBtn.text = "Wait..."
        } else {
            connectBtn.isEnabled = true
            connectBtn.text = "CONNECT!"
        }
    }

    /*
    Save the url in the database
     */
    private fun saveUrl() {
        // TODO: save url in db
        val queue = Volley.newRequestQueue(this)
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

    /*
    Make a toast
     */
    private fun toast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}