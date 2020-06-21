package com.ronyut.flightmobileapp.API

import com.ronyut.flightmobileapp.FlightData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostRepo(private val baseUrl: String) {
    var client: PostApi = ServiceBuilder.init(baseUrl).webservice

    suspend fun postFlightData(flightData: FlightData?) = client.postFlightData(flightData)
}