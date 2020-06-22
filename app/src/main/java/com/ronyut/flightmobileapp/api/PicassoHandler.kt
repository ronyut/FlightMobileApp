package com.ronyut.flightmobileapp.api

import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.lang.Exception

class PicassoHandler {
    companion object {

        // get image from url and put in ImageView
        fun getImage(url: String?, imgView: ImageView?, onResult: (String?) -> Unit) {
            Picasso.get()
                .load(url + RequestHandler.URL_API_SCREENSHOT)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .noPlaceholder()
                .into(imgView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        onResult(null)
                    }

                    override fun onError(e: Exception?) {
                        // TODO: check timeout in Picasso
                        onResult(e?.message)
                    }
                })
        }
    }
}