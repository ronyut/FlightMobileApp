package com.ronyut.flightmobileapp

import com.ronyut.flightmobileapp.API.PostRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class Util {
    companion object {
        fun isServerOnline(
            flightData: FlightData?,
            url: String,
            util: Util,
            onResult: (Response<Unit>) -> Unit
        ) = util.postFlightData(flightData, url, onResult)

        fun postFlightData(
            flightData: FlightData?,
            url: String,
            util: Util,
            onResult: (Response<Unit>) -> Unit
        ) = util.postFlightData(flightData, url, onResult)
    }

    /*
   Post flight data to server
    */
    fun postFlightData(
        flightData: FlightData?, url: String, onResult: (Response<Unit>) -> Unit
    ): Boolean {
        var result = false;

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = PostRepo(url)
                val response = repository.postFlightData(flightData)

                if (response.isSuccessful) {
                    result = true
                } else {
                    throw Exception("Something went wrong")
                }
                onResult(response)
            } catch (e: Exception) {
                throw e
            }
        }

        return result
    }
}