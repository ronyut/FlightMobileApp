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
//  save url +
//  check if `/` at the end erroring
//  landscape view
//  vertical taskbar
//  check timeout erroring (screenshot + command)
//  Update buttons on resume in this page

class MainActivity : AppCompatActivity()/*, CoroutineScope by MainScope() ToDO */ {
    companion object {
        const val EXTRA_MESSAGE = "com.ronyut.flightmobileapp.URL"
        const val NO_ERROR = ""
    }

    private lateinit var urlViewModel: UrlViewModel
    private var baseUrl = ""
    //private var job: Job? = Job() TODO

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
        //launch {
        PicassoHandler.getImage(baseUrl, screenshotTest) { msg ->
            toggleButton(false)

            if (msg != null) {
                toast(msg) // TODO: change to "server offline" / timeout
            } else {
                saveUrl()
                connectionSuccessful()
            }
        }
        //}
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
    private fun saveUrl() {
        val url = Url(baseUrl, "now")
        urlViewModel.insert(url)
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

    // cancel job to stop getting screenshots when app is minimized
    override fun onDestroy() {
        super.onDestroy()
        //cancel() TODO
    }
}