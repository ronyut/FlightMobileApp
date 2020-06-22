package com.ronyut.flightmobileapp.api

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.*
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException

class RequestHandler(context: Context, private val baseUrl: String?) {
    companion object {
        const val URL_API_COMMAND = "/api/command"
        const val URL_API_SCREENSHOT = "/screenshot"
        const val TIMEOUT_MS = 3305
    }

    // Instantiate the RequestQueue.
    private val queue: RequestQueue = Volley.newRequestQueue(context)

    suspend fun postFlightData(flightData: FlightData, onResult: (Int?) -> Unit): Unit =
        suspendCoroutine { cont ->
            val url = baseUrl + URL_API_COMMAND
            println(">> URL: $url")

            // Send a json post request
            val requestJson = makeJson(flightData)
            val request = MyJsonObjectRequest(
                Request.Method.POST, url, requestJson,
                Response.Listener {
                    // get status code
                    onResult(it?.getString("code")?.toInt())
                },
                Response.ErrorListener { err ->
                    val time = err.networkTimeMs / 1000
                    val e = when {
                        err.networkTimeMs > 9500 -> TimeoutException("Intermediate server timed out ($time seconds)")
                        err.networkResponse?.statusCode == null -> Exception(err.message)
                        else -> ServerUpException(err.networkResponse.statusCode.toString())
                    }
                    cont.resumeWithException(e)
                })

            // set timeout to 10 seconds
            val policy = DefaultRetryPolicy(
                TIMEOUT_MS, // actually 10 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            request.retryPolicy = policy

            // Add the request to the RequestQueue.
            queue.add(request)
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
