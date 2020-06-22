package com.ronyut.flightmobileapp.api

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import org.json.JSONObject

class MyJsonObjectRequest(
    method: Int,
    url: String?,
    jsonRequest: JSONObject?,
    listener: Response.Listener<JSONObject?>?,
    errorListener: Response.ErrorListener?
) : JsonRequest<JSONObject>(
    method,
    url,
    jsonRequest?.toString(),
    listener,
    errorListener
) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        return try {

            val result = JSONObject()
            result.put("code", response.statusCode)
            Response.success(
                result,
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }
}