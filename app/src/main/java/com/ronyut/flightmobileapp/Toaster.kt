package com.ronyut.flightmobileapp

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Toaster(private val applicationContext: Context) {
    private var toaster: String? = null
    private var waitingToaster: String? = null
    private var isToasterAvailable = true

    /*
    Make a toast
     */
    fun toast(text: String?) {
        fun makeToast() {
            if (isToasterAvailable) {
                isToasterAvailable = false
                toaster = waitingToaster
                Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
                GlobalScope.launch {
                    delay(3000)
                    isToasterAvailable = true
                }
            }
        }

        waitingToaster = text
        makeToast()
    }
}