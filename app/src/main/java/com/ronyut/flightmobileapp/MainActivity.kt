package com.ronyut.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ronyut.flightmobileapp.api.PicassoHandler
import com.ronyut.flightmobileapp.room.UrlViewModel
import kotlinx.android.synthetic.main.activity_main.*

// TODO:
//  VVVVVVVVVVVVVVVVVVVVsave url
//  VVVVVVVVVVV landscape view
//  VVVVVVVVVVVVVVVVVVVvertical taskbar
//  VVVVVVVVVVVVVVVVVVVVVV       check timeout erroring (screenshot)
//  VVVVVVVVVV Update buttons on resume in this page
//  VVVVVVVVVVV sarah server + john server
//  coding style
//  VVVVVVVVVVVVVV resolve all todos
//  comments

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MESSAGE = "com.ronyut.flightmobileapp.URL"
        const val NO_ERROR = ""
    }

    private lateinit var toaster: Toaster
    private lateinit var db: UrlDbManager
    private var baseUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        db = UrlDbManager(applicationContext)
        toaster = Toaster(applicationContext)

        updateView()
        setConnectBtnListener()
    }

    // set the connection button listener
    private fun setConnectBtnListener() {
        connectBtn.setOnClickListener {
            baseUrl = textboxNewServer.text.toString()
            toggleButton(true)

            when (val err = Util.checkUrlValidity(baseUrl)) {
                NO_ERROR -> checkConnection()
                else -> {
                    toast(err)
                    toggleButton(false)
                }
            }
        }

        btn1.setOnClickListener {
            textboxNewServer.setText(btn1.text)
        }

        btn2.setOnClickListener {
            textboxNewServer.setText(btn2.text)
        }

        btn3.setOnClickListener {
            textboxNewServer.setText(btn3.text)
        }

        btn4.setOnClickListener {
            textboxNewServer.setText(btn4.text)
        }

        btn5.setOnClickListener {
            textboxNewServer.setText(btn5.text)
        }
    }

    /*
    Check if the server is up
     */
    private fun checkConnection() {
        val picasso = PicassoHandler(this)
        picasso.getImage(baseUrl, screenshotTest) { msg ->
            toggleButton(false)
            if (msg != null) {
                toast("Server unavailable")
            } else {
                connectionSuccessful()
            }
        }
    }

    /*
    If the server is online, go to the dashboard
     */
    private fun connectionSuccessful() {
        toast("Connected! Loading...")
        saveUrl()
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
    private fun saveUrl(url: String = baseUrl) {
        db.addUrl(baseUrl)
        updateView()
    }

    private fun updateView() {
        val urls = db.getDb()
        var last = 0
        for ((i, url) in urls.withIndex()) {
            println("Make visible: ${i + 1}")
            when (i + 1) {
                1 -> setBtn(btn1, url)
                2 -> setBtn(btn2, url)
                3 -> setBtn(btn3, url)
                4 -> setBtn(btn4, url)
                5 -> setBtn(btn5, url)
            }
            last = i + 1
        }

        for (i in last + 1 until 6) {
            println("Make Gone: $i")
            when (i) {
                1 -> setBtn(btn1, "", View.GONE)
                2 -> setBtn(btn2, "", View.GONE)
                3 -> setBtn(btn3, "", View.GONE)
                4 -> setBtn(btn4, "", View.GONE)
                5 -> setBtn(btn5, "", View.GONE)
            }
        }
    }

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

    /*
    Make a toast
     */
    private fun toast(text: String?) {
        toaster.toast(text)
    }
}