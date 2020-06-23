package com.ronyut.flightmobileapp

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
    Toast controller that enqueues only one toast at a time
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class Toaster(private val applicationContext: Context) {
    private var toaster: String? = null
    private var waitingToaster: String? = null
    private var isToasterAvailable = true

    companion object {
        const val TOAST_DELAY = 3000L
    }

    /*
    Make a toast
     */
    fun toast(text: String?) {
        /*
        This function ensures that there's always only one toast waiting in the queue
        We do it because we don't want to flood the user with tons of toasts one after another
        */
        fun makeToast() {
            if (isToasterAvailable) {
                isToasterAvailable = false
                toaster = waitingToaster
                // Show the toast
                Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
                // delay next toast for 3 seconds
                GlobalScope.launch {
                    delay(TOAST_DELAY)
                    isToasterAvailable = true
                }
            }
        }

        waitingToaster = text
        makeToast()
    }
}