package com.ronyut.flightmobileapp.api

import android.content.Context
import android.widget.ImageView
import com.ronyut.flightmobileapp.api.RequestHandler.Companion.TIMEOUT_MS
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.OkHttp3Downloader
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class PicassoHandler(val context: Context, val isCheck: Boolean = false) {
    private lateinit var picasso: Picasso

    init {
        val timeout = TIMEOUT_MS.toLong() + 500

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            .build()

        picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClient)).build()
    }

    // get image from url and put in ImageView
    fun getImage(url: String?, imgView: ImageView?, onResult: (String?) -> Unit) {
        picasso.isLoggingEnabled = true
        picasso.load(url + RequestHandler.URL_API_SCREENSHOT)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .noPlaceholder()
            .into(imgView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    onResult(null)
                    println("Picasso OKAY!")
                }

                override fun onError(e: Exception?) {
                    if (e != null) println("Picasso exception: ${e::class.qualifiedName}")
                    println("-> ${e?.message}")

                    val msg = when (e) {
                        is IOException -> "Server returned invalid image"
                        is TimeoutException -> "Screenshot timeout"
                        else -> "Could not fetch screenshot"
                    }
                    onResult(msg)
                }
            })
    }
}