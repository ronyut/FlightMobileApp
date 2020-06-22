package com.ronyut.flightmobileapp

class Util {
    companion object {
        const val CONSIDER_TEXT = ". Consider returning to main menu to reconnect"

        /*
        Convert a status code into an explanatory string
         */
        fun codeToText(code: Int?): String {
            var text = when (code) {
                200 -> "Success"
                302 -> "Flight Gear returned invalid value(s)"
                304 -> "Not modified"
                400 -> "Sent invalid parameters"
                503 -> "FlightGear is not responding"
                else -> "Something went wrong"
            }
            text += CONSIDER_TEXT
            return text
        }
    }
}