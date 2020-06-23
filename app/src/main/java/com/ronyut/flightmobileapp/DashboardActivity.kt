package com.ronyut.flightmobileapp

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.ronyut.flightmobileapp.api.FlightData
import com.ronyut.flightmobileapp.api.PicassoHandler
import com.ronyut.flightmobileapp.api.RequestHandler
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.*
import ninja.eigenein.joypad.JoypadView
import kotlin.math.abs

/*
    The dashboard activity that control the FG simulator
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class DashboardActivity : AppCompatActivity(), JoypadView.Listener, CoroutineScope by MainScope() {
    companion object {
        const val DELAY_SCREENSHOT = 200L
    }

    private var baseUrl: String? = null
    private var jobScreenshot: Job? = Job()
    private var jobPost: Job? = Job()

    private var aileron: Double = 0.0
    private var elevator: Double = 0.0
    private var rudder: Double = 0.0
    private var throttle: Double = 0.0

    private var imageLoaded = true
    private lateinit var toaster: Toaster
    private lateinit var requestHandler: RequestHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Hide the annoying and redundant action bar
        supportActionBar?.hide()
        // create the toaster that manages all toasts
        toaster = Toaster(applicationContext)

        // get base api URL from previous activity
        when (val url = intent.getStringExtra(MainActivity.EXTRA_MESSAGE)) {
            null -> toast("Could not fetch URL") // impossible
            else -> {
                baseUrl = url
                requestHandler = RequestHandler(this, baseUrl)
            }
        }

        setListeners()
    }

    // set all listeners
    private fun setListeners() {
        setRudderListener()
        setThrottleListener()
        joypad.setListener(this)
        getScreenshot()
    }

    // cancel job to stop getting screenshots when app is minimized
    override fun onStop() {
        super.onStop()
        println("Dashboard stopped")
        jobScreenshot?.cancel()
        jobPost?.cancel()
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

        if (jobScreenshot == null) {
            jobScreenshot = Job()
            getScreenshot()
        }

        if (jobPost == null) jobPost = Job()
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

                // check if no other screenshot fetching is in progress
                if (imageLoaded) {
                    imageLoaded = false
                    picasso.getImage(baseUrl, screenshot) { msg ->
                        if (msg != null && jobScreenshot?.isActive!!) toast(msg + Util.CONSIDER_TEXT)
                        imageLoaded = true
                    }
                }
                delay(DELAY_SCREENSHOT)
            }
        }
    }

    /*
    Post the flight data and makes a toast in case of error
     */
    private fun sendFlightData() {
        val flightData = FlightData(aileron, elevator, rudder, throttle)

        jobPost = launch {
            jobPost?.ensureActive()
            requestHandler.postFlightData(flightData) { msg, isSuccess ->
                if (!isSuccess && jobPost?.isActive!!) toast(msg + Util.CONSIDER_TEXT)
            }
        }
    }

    /*
    When the joystick handle is released: reset joystick values
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

        // minimum 1% change
        if (changeX >= 0.01) {
            println("Change X: $changeX")
            aileron = xNew
            doSend = true
        }

        // minimum 1% change
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
                rudder_val.text = newProgress.toString()
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

    /*
    Set the throttle seekbar listener
     */
    private fun setThrottleListener() {
        throttle_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newProgress = progress.toDouble() / 100
                throttle_val.text = newProgress.toString()
                val change = abs((throttle * 100) - progress)
                if (change >= 1) {
                    throttle = newProgress
                    // run the co-routine to post flight data
                    sendFlightData()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // show toast
    private fun toast(text: String?) {
        toaster.toast(text)
    }
}