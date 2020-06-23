package com.ronyut.flightmobileapp.api

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.*
import com.ronyut.flightmobileapp.Util
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import java.io.IOException
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

    suspend fun postFlightData(flightData: FlightData, onResult: (String?, Boolean) -> Unit): Unit =
        suspendCoroutine {
            val url = baseUrl + URL_API_COMMAND
            println(">> URL: $url")

            // Send a json post request
            val requestJson = makeJson(flightData)
            val request = MyJsonObjectRequest(
                Request.Method.POST, url, requestJson,
                Response.Listener {
                    // get status code
                    val code = it?.getString("code")?.toInt()
                    var isSuccess = true
                    if (code != 200) isSuccess = false
                    onResult(Util.codeToText(code), isSuccess)
                },
                Response.ErrorListener { err ->
                    val code = err?.networkResponse?.statusCode?.toInt()
                    val e = when {
                        err.networkTimeMs > 9500 -> TimeoutException("Intermediate server timed out")
                        err is IOException -> Exception("Could not receive server's response")
                        code != null -> Exception(Util.codeToText(code))
                        else -> Exception("Corrupt response from server")
                    }
                    //cont.resumeWithException(e)
                    onResult(e.message, false)
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
