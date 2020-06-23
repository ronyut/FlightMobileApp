package com.ronyut.flightmobileapp.api

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.OkHttp3Downloader
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit

/*
    This class is responsible for handling Picasso requests - fetching screenshots from the server
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class PicassoHandler(context: Context) {
    private var picasso: Picasso

    companion object {
        // Set the timeout for some reason 4 sec => 10 sec
        const val PICASSO_TIMEOUT = 4000L
    }

    init {
        // set the http client once to timeout after 10 seconds
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(PICASSO_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(PICASSO_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()

        // set Picasso's http client

        picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClient)).build()
    }

    // Get image from url and inject into screenshot ImageView
    fun getImage(url: String?, imgView: ImageView?, onResult: (String?) -> Unit) {
        // For developing purposes
        picasso.isLoggingEnabled = true

        // Load the image
        picasso.load(url + RequestHandler.URL_API_SCREENSHOT)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .noPlaceholder()
            .into(imgView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    onResult(null)
                }

                override fun onError(e: Exception?) {
                    // Printing exception details
                    if (e != null) println("Picasso exception: ${e::class.qualifiedName}")
                    println("-> ${e?.message}")

                    val msg = when (e) {
                        is IOException -> "Server disconnected"
                        else -> "Server disconnected (Error p_1)"
                    }
                    onResult(msg)
                }
            })
    }
}