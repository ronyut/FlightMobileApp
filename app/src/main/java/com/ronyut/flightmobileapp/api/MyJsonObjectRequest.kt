package com.ronyut.flightmobileapp.api

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import org.json.JSONObject

/*
    Custom JsonObjectRequest that return a json with the server's response code
    Author: Rony Utevsky
    Date: 23-06-2020
 */
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