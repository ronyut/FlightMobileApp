package com.ronyut.flightmobileapp

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ronyut.flightmobileapp.api.FlightData
import com.ronyut.flightmobileapp.api.PicassoHandler
import com.ronyut.flightmobileapp.api.RequestHandler
import com.ronyut.flightmobileapp.api.ServerUpException
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.*
import ninja.eigenein.joypad.JoypadView
import kotlin.math.abs


class DashboardActivity : AppCompatActivity(), JoypadView.Listener, CoroutineScope by MainScope() {
    private var baseUrl: String? = null
    private var jobScreenshot: Job? = Job()
    private var jobPost: Job? = Job()

    private var aileron: Double = 0.0
    private var elevator: Double = 0.0
    private var rudder: Double = 0.0
    private var throttle: Double = 0.5

    private var imageLoaded = true
    private lateinit var toaster: Toaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()
        toaster = Toaster(applicationContext)

        // get base api URL
        when (val url = intent.getStringExtra(MainActivity.EXTRA_MESSAGE)) {
            null -> toast("Could not fetch URL") // impossible
            else -> baseUrl = url
        }
        setListeners()
    }

    // set all listeners
    private fun setListeners() {
        setRudderListener()
        joypad.setListener(this)
        getScreenshot()
    }

    // cancel job to stop getting screenshots when app is minimized
    override fun onStop() {
        super.onStop()
        println("Dashboard stopped")
        jobScreenshot?.cancel()
    }

    // cancel job to stop getting screenshots when app is minimized
    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    // resume job to resume getting screenshots when activity is brought back to background
    override fun onRestart() {
        super.onRestart()
        println("Dashboard restarted")
        jobScreenshot = Job()
        getScreenshot()
    }

    /*
    An async function for getting a screenshot
     */
    private fun getScreenshot() {
        val picasso = PicassoHandler(this)
        // run the co-routine to get a screenshot
        jobScreenshot = launch {
            while (true) {
                jobScreenshot?.ensureActive()

                if (imageLoaded) {
                    println("screenshot!")

                    imageLoaded = false
                    picasso.getImage(baseUrl, screenshot) { msg ->
                        if (msg != null) toast(msg)
                        imageLoaded = true
                    }
                }

                delay(1000)
            }
        }
    }

    /*
    Post the flight data and makes a toast in case of error
     */
    private fun sendFlightData() {
        val flightData = FlightData(aileron, elevator, rudder, throttle)

        jobPost = launch {
            try {
                val requestHandler = RequestHandler(this@DashboardActivity, baseUrl)
                requestHandler.postFlightData(flightData) { code ->
                    if (code != 200) toast(Util.codeToText(code))
                }
            } catch (e: Exception) {
                when (e) {
                    is ServerUpException -> toast(Util.codeToText(e.message?.toInt()))
                    else -> toast(e.message + Util.CONSIDER_TEXT)
                }
            }
        }
    }

    /*
    When the joystick handle is released
     */
    override fun onUp() {
        aileron = 0.0
        elevator = 0.0
    }

    /*
    When the joystick handle is being moved
     */
    override fun onMove(dist: Float, x: Float, y: Float) {
        var doSend = false
        val xNew = x.toDouble()
        val yNew = y.toDouble()

        val changeX = abs(xNew - aileron) / 2
        val changeY = abs(yNew - elevator) / 2

        if (changeX >= 0.01) {
            println("Change X: $changeX")
            aileron = xNew
            doSend = true
        }

        if (changeY >= 0.01) {
            println("Change Y: $changeY")
            elevator = yNew
            doSend = true
        }

        if (doSend) {
            sendFlightData()
        }
    }

    /*
    Set the rudder seekbar listener
     */
    private fun setRudderListener() {
        rudder_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newProgress = (progress.toDouble() - 50) / 50
                val change = abs((rudder * 50) + 50 - progress)
                if (change >= 1) {
                    rudder = newProgress
                    // run the co-routine to post flight data
                    sendFlightData()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun toast(text: String?) {
        toaster.toast(text)
    }
}