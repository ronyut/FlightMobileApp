package com.ronyut.flightmobileapp

import android.webkit.URLUtil

/*
    Utility class with useful general functions
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class Util {
    companion object {
        const val CONSIDER_TEXT = ". Consider returning to main menu to reconnect"

        /*
        Convert a status code into an explanatory string
         */
        fun codeToText(code: Int?): String {
            return when (code) {
                200 -> "Success"
                302 -> "Flight Gear returned invalid value(s)"
                304 -> "Server could not update simulator"
                400 -> "Sent invalid parameters" // irrelevant
                500 -> "Intermediate server internal error" // irrelevant
                503 -> "FlightGear is not responding"
                else -> "Something went wrong (Error u_0)"
            }
        }

        /*
        Check if url is valid and show toast
         */
        fun checkUrlValidity(url: String): String {
            var err = MainActivity.NO_ERROR

            if (url == "") {
                err = "Empty URL"
            } else if (url.take(5) == "https") {
                err = "HTTPS protocol is not supported"
            } else if (!URLUtil.isValidUrl(url)) {
                err = "Invalid URL"
            } else if (url.last().toString() == "/") {
                err = "Please remove trailing slash from URL"
            }

            return err
        }

        // Replace localhost
        fun replaceLocalhost(url: String) = url.replace("localhost", "10.0.2.2")

    }
}