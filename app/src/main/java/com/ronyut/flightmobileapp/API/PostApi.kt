package com.ronyut.flightmobileapp.API

import com.ronyut.flightmobileapp.FlightData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PostApi {

    @Headers("Content-Type: application/json")
    @POST("api/command")
    suspend fun postFlightData(@Body flightData: FlightData?): Response<Unit>
}