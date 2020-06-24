package com.ronyut.flightmobileapp.api

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.*
import com.ronyut.flightmobileapp.Util
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import java.io.IOException

/*
    This class is responsible for handling json POST requests to the server
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class RequestHandler(context: Context, private val baseUrl: String?) {
    private val queue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        const val URL_API_COMMAND = "/api/command"
        const val URL_API_SCREENSHOT = "/screenshot"
        const val TIMEOUT_MS = 3305
        const val MAX_NETWORK_TIME_MS = 9500
        const val CODE_SUCCESS = 200
    }

    // Post Flight data to server
    suspend fun postFlightData(flightData: FlightData, onResult: (String?, Boolean) -> Unit): Unit =
        suspendCoroutine {
            val url = baseUrl + URL_API_COMMAND

            // Send a json post request
            val requestJson = makeJson(flightData)
            var request = MyJsonObjectRequest(
                Request.Method.POST, url, requestJson,
                Response.Listener {
                    // get status code
                    val code = it?.getString("code")?.toInt()
                    var isSuccess = true
                    if (code != CODE_SUCCESS) isSuccess = false
                    onResult(Util.codeToText(code), isSuccess)
                },
                Response.ErrorListener { err ->
                    // Printing exception details
                    if (err != null) println("Post exception: ${err::class.qualifiedName}")
                    println("-> ${err?.message}")

                    val code = err?.networkResponse?.statusCode
                    val msg = when {
                        err.networkTimeMs > MAX_NETWORK_TIME_MS -> "Intermediate server timed out"
                        err is IOException -> "Could not fetch server's response"
                        code != null -> Util.codeToText(code)
                        else -> null //
                    }
                    onResult(msg, false)
                })

            request = prepareTimeoutPolicy(request)

            // Add the request to the RequestQueue.
            queue.add(request)
        }

    // Set the request's policy so it will timeout after 10 seconds
    private fun prepareTimeoutPolicy(request: MyJsonObjectRequest): MyJsonObjectRequest {
        val policy = DefaultRetryPolicy(
            TIMEOUT_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        request.retryPolicy = policy
        return request
    }

    // Prepare the json that will be sent
    private fun makeJson(flightData: FlightData) =
        JSONObject()
            .put("aileron", flightData.aileron)
            .put("rudder", flightData.rudder)
            .put("throttle", flightData.throttle)
            .put("elevator", flightData.elevator)

}
