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
                304 -> "Not modified"
                400 -> "Sent invalid parameters"
                500 -> "Intermediate server internal error"
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
            } else if (!URLUtil.isValidUrl(url)) {
                err = "Invalid URL"
            } else if (url.last().toString() == "/") {
                err = "Please remove trailing slash from URL"
            }

            return err
        }
    }
}