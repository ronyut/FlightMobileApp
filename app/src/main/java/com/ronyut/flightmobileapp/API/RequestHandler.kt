package com.ronyut.flightmobileapp.API

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.*
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import kotlin.coroutines.resumeWithException

class RequestHandler(context: Context, private val baseUrl: String?) {
    companion object {
        const val URL_API_COMMAND = "/api/command"
        const val URL_API_SCREENSHOT = "/screenshot"
    }

    // Instantiate the RequestQueue.
    private val queue: RequestQueue = Volley.newRequestQueue(context)

    suspend fun postFlightData(flightData: FlightData, onResult: () -> Unit): Unit =
        suspendCoroutine { cont ->
            val url = baseUrl + URL_API_COMMAND
            println(">> URL: $url")

            // Send a json post request
            val requestJson = makeJson(flightData)
            val stringRequest = MyJsonObjectRequest(
                Request.Method.POST, url, requestJson,
                Response.Listener {
                    onResult()
                },
                Response.ErrorListener {
                    println(it.message)
                    if (it.networkResponse?.statusCode == null) {
                        cont.resumeWithException(Exception(it.message))
                    } else {
                        cont.resumeWithException(
                            ServerUpException(
                                it.networkResponse.statusCode.toString()
                            )
                        )
                    }
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }

    private fun makeJson(flightData: FlightData): JSONObject {
        val json = JSONObject()
        json.put("aileron", flightData.aileron)
        json.put("rudder", flightData.rudder)
        json.put("throttle", flightData.throttle)
        json.put("elevator", flightData.elevator)

        return json
    }
}
