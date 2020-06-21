package com.ronyut.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil.isValidUrl
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.ronyut.flightmobileapp.API.RequestHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

        connectBtn.setOnClickListener {
            baseUrl = textboxNewServer.text.toString()
            toggleButton(true)

            if (!isValidUrl(baseUrl)) {
                toast("Invalid URL")
                toggleButton(false)
            } else {
                checkConnection()
            }
        }

    }

    /*
    Check if the server is up
     */
    private fun checkConnection() {
        launch {
            try {
                val flightData = FlightData(0.0, 0.0, 0.0, 0.0)
                val requestHandler = RequestHandler(this@MainActivity, baseUrl)
                requestHandler.postFlightData(flightData) {
                    connectionSuccessful("200")
                }
            } catch (e: Exception) {
                when (e) {
                    is ServerUpException -> connectionSuccessful(e.message)
                    else -> toast("Server offline " + e.message)
                }
            } finally {
                toggleButton(false)
                // TODO: refresh buttons - url updates
                saveUrl()
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