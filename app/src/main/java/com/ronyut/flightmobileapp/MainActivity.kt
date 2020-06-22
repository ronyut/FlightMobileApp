package com.ronyut.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil.isValidUrl
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ronyut.flightmobileapp.api.PicassoHandler
import com.ronyut.flightmobileapp.room.Url
import com.ronyut.flightmobileapp.room.UrlListAdapter
import com.ronyut.flightmobileapp.room.UrlViewModel
import kotlinx.android.synthetic.main.activity_main.*

// TODO:
//  save url
//  landscape view
//  vertical taskbar
//  DONE                                                    check timeout erroring (screenshot)
//  Update buttons on resume in this page
//  sarah server + john server
//  coding style
//  resolve all todos
class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MESSAGE = "com.ronyut.flightmobileapp.URL"
        const val NO_ERROR = ""
    }

    private lateinit var urlViewModel: UrlViewModel
    private var baseUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        setRecycleView()
        setConnectBtnListener()
    }

    private fun setRecycleView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = UrlListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //urlViewModel = ViewModelProvider(this).get(UrlViewModel::class.java)
        urlViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(UrlViewModel::class.java)

        urlViewModel.allUrls.observe(this, Observer { urls ->
            // Update the cached copy of the urls in the adapter.
            urls?.let { adapter.setUrls(it) }
        })
    }

    // set the connection button listener
    private fun setConnectBtnListener() {
        connectBtn.setOnClickListener {
            baseUrl = textboxNewServer.text.toString()
            toggleButton(true)

            when (val err = checkUrlValidity()) {
                NO_ERROR -> checkConnection()
                else -> {
                    toast(err)
                    toggleButton(false)
                }
            }
        }

        testBtn.setOnClickListener {
            val url = testBtn.text.toString()
            saveUrl(url)
            toast("added $url")
        }
    }

    /*
    Check if url is valid and show toast
     */
    private fun checkUrlValidity(): String {
        var err = NO_ERROR

        if (baseUrl == "") {
            err = "Empty URL"
        } else if (!isValidUrl(baseUrl)) {
            err = "Invalid URL"
        } else if (baseUrl.last().toString() == "/") {
            err = "Please remove trailing slash from URL"
        }

        return err
    }

    /*
    Check if the server is up
     */
    private fun checkConnection() {
        val picasso = PicassoHandler(this)
        picasso.getImage(baseUrl, screenshotTest) { msg ->
            toggleButton(false)

            if (msg != null) {
                toast(msg) // TODO: change to "server offline" / timeout
            } else {
                saveUrl()
                connectionSuccessful()
            }
        }
    }

    /*
    If the server is online, go to the dashboard
     */
    private fun connectionSuccessful() {
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
        val link = Url(url, "now") // TODO CHANGE `NOW`
        urlViewModel.insert(link)
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
}