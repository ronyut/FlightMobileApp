package com.ronyut.flightmobileapp.api

/*
    Flight data that will be converted into json and sent to the server
    Author: Rony Utevsky
    Date: 23-06-2020
 */
data class FlightData(
    val aileron: Double? = null,
    val elevator: Double? = null,
    val rudder: Double? = null,
    val throttle: Double? = null
)